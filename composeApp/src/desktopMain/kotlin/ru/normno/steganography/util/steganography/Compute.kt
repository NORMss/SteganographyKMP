package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

object Compute {
    fun computeCapacity(cover: BufferedImage): Int {
        return (cover.width * cover.height) / 4 * 3
    }

    fun computePSNR(original: BufferedImage, recovered: BufferedImage): Double {
        require(original.width == recovered.width && original.height == recovered.height) { "Размеры изображений должны совпадать!" }

        var mse = 0.0
        val width = original.width
        val height = original.height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val origPixel = original.getRGB(x, y) and 0xFF
                val recPixel = recovered.getRGB(x, y) and 0xFF
                mse += (origPixel - recPixel).toDouble().pow(2)
            }
        }

        mse /= (width * height)
        return if (mse == 0.0) Double.POSITIVE_INFINITY else 10 * log10(255.0.pow(2) / mse)
    }

    fun chiSquareTest(image: BufferedImage, blockSize: Int): Double {
        val width = image.width
        val height = image.height
        var chiSquare = 0.0

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                val histogram = IntArray(256)
                var totalPixels = 0

                for (i in 0 until blockSize) {
                    for (j in 0 until blockSize) {
                        if (x + i < width && y + j < height) {
                            val rgb = image.getRGB(x + i, y + j)
                            val gray = (rgb shr 16 and 0xFF) * 0.3 +
                                    (rgb shr 8 and 0xFF) * 0.59 +
                                    (rgb and 0xFF) * 0.11
                            histogram[gray.toInt()]++
                            totalPixels++
                        }
                    }
                }

                if (totalPixels > 0) { // Защита от деления на ноль
                    val expected = totalPixels / 256.0
                    chiSquare += histogram.sumOf { if (expected > 0) ((it - expected).pow(2)) / expected else 0.0 }
                }
            }
        }
        return chiSquare
    }

    fun aumpTest(image: BufferedImage, blockSize: Int, degree: Int): Double {
        val width = image.width
        val height = image.height
        var beta = 0.0
        val q = degree + 1

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                val pixels = mutableListOf<Int>()
                for (i in 0 until blockSize) {
                    for (j in 0 until blockSize) {
                        if (x + i < width && y + j < height) {
                            pixels.add(image.getRGB(x + i, y + j) and 0xFF)
                        }
                    }
                }

                val X = pixels.map { it.toDouble() }.toDoubleArray()
                val Xpred = polynomialPrediction(X, blockSize, degree)
                val residuals = X.zip(Xpred) { a, b -> a - b }
                val variance = residuals.sumOf { it * it } / residuals.size

                val Xbar = X.map { it + 1 - 2 * (it % 2) }
                beta += X.zip(Xbar) { a, b -> (a - b) * residuals[pixels.indexOf(a.toInt())] }.sum()
            }
        }
        return beta
    }

    fun aumpTest(image: BufferedImage, blockSize: Int): Double {
        val width = image.width
        val height = image.height
        var beta = 0.0

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                val pixels = mutableListOf<Int>()
                for (i in 0 until blockSize) {
                    for (j in 0 until blockSize) {
                        if (x + i < width && y + j < height) {
                            pixels.add(image.getRGB(x + i, y + j) and 0xFF)
                        }
                    }
                }

                val mean = pixels.average()
                val residuals = pixels.map { it - mean }
                val variance = residuals.sumOf { it * it } / pixels.size

                beta += variance
            }
        }
        return beta
    }

    private fun polynomialPrediction(X: DoubleArray, blockSize: Int, degree: Int): DoubleArray {
        val q = degree + 1
        val H = Array(blockSize) { i -> DoubleArray(q) { j -> (i + 1.0).pow(j) } }
        val Y = X.copyOf()

        val p = leastSquares(H, Y)
        return H.map { row -> row.zip(p).sumOf { it.first * it.second } }.toDoubleArray()
    }

    private fun leastSquares(H: Array<DoubleArray>, Y: DoubleArray): DoubleArray {
        val q = H[0].size
        val HtH = Array(q) { DoubleArray(q) }
        val HtY = DoubleArray(q)

        for (i in 0 until q) {
            for (j in 0 until q) {
                HtH[i][j] = H.sumOf { it[i] * it[j] }
            }
            HtY[i] = H.indices.sumOf { H[it][i] * Y[it] }
        }
        return solveLinearSystem(HtH, HtY)
    }

    private fun solveLinearSystem(A: Array<DoubleArray>, B: DoubleArray): DoubleArray {
        val n = A.size
        val x = DoubleArray(n)
        val L = Array(n) { DoubleArray(n) }
        val U = Array(n) { DoubleArray(n) }

        for (i in 0 until n) {
            for (j in 0 until n) {
                if (j < i) {
                    L[j][i] = 0.0
                } else {
                    L[j][i] = A[j][i]
                    for (k in 0 until i) {
                        L[j][i] -= L[j][k] * U[k][i]
                    }
                    U[j][i] = if (j == i) 1.0 else 0.0
                }
            }
            for (j in 0 until n) {
                if (j < i) {
                    U[i][j] = 0.0
                } else {
                    U[i][j] = A[i][j] / L[i][i]
                    for (k in 0 until i) {
                        U[i][j] -= (L[i][k] * U[k][j]) / L[i][i]
                    }
                }
            }
        }
        val Y = DoubleArray(n)
        for (i in 0 until n) {
            Y[i] = B[i]
            for (j in 0 until i) {
                Y[i] -= L[i][j] * Y[j]
            }
            Y[i] /= L[i][i]
        }
        for (i in n - 1 downTo 0) {
            x[i] = Y[i]
            for (j in i + 1 until n) {
                x[i] -= U[i][j] * x[j]
            }
        }
        return x
    }

    fun compressionAnalysis(original: BufferedImage, compressed: BufferedImage): Double {
        val width = original.width
        val height = original.height
        var diffSum = 0.0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val origPixel = original.getRGB(x, y) and 0xFF
                val compPixel = compressed.getRGB(x, y) and 0xFF
                diffSum += (origPixel - compPixel).toDouble().pow(2)
            }
        }

        return sqrt(diffSum / (width * height))
    }
}