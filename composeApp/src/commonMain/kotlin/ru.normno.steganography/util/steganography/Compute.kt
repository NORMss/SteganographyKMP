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
        var chiSquareSum = 0.0
        var totalBlocks = 0

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                val histogram = IntArray(256)
                var totalPixels = 0

                for (i in 0 until blockSize) {
                    for (j in 0 until blockSize) {
                        val xi = x + i
                        val yj = y + j
                        if (xi < width && yj < height) {
                            val pixel = image.getRGB(xi, yj) and 0xFF // grayscale
                            histogram[pixel]++
                            totalPixels++
                        }
                    }
                }

                if (totalPixels > 0) {
                    // Классический LSB chi-square тест
                    for (i in 0 until 128) {
                        val observed0 = histogram[2 * i]
                        val observed1 = histogram[2 * i + 1]
                        val expected = (observed0 + observed1) / 2.0

                        if (expected > 0) {
                            chiSquareSum += ((observed0 - expected).pow(2)) / expected
                            chiSquareSum += ((observed1 - expected).pow(2)) / expected
                        }
                    }
                    totalBlocks++
                }
            }
        }

        return if (totalBlocks > 0) chiSquareSum / totalBlocks else 0.0
    }

    fun aumpTest(image: BufferedImage, blockSize: Int, degree: Int): Double {
        val width = image.width
        val height = image.height
        var beta = 0.0
        val q = degree + 1
        var totalBlocks = 0

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                val pixels = mutableListOf<Double>()
                for (i in 0 until blockSize) {
                    for (j in 0 until blockSize) {
                        if (x + i < width && y + j < height) {
                            pixels.add((image.getRGB(x + i, y + j) and 0xFF).toDouble())
                        }
                    }
                }

                if (pixels.size < q) continue // Пропуск маленьких блоков

                val X = pixels.toDoubleArray()
                val Xpred = polynomialPrediction(X, degree)
                val residuals = X.zip(Xpred) { a, b -> a - b }
                val variance = residuals.sumOf { it * it } / residuals.size

                val Xbar = X.map { it + 1 - 2 * (it % 2) } // Флип LSB
                val weight = sqrt(variance / residuals.size)

                beta += weight * X.zip(Xbar) { a, b -> (a - b) * residuals[pixels.indexOf(a)] }.sum()
                totalBlocks++
            }
        }

        return if (totalBlocks > 0) beta / totalBlocks else beta
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

    private fun polynomialPrediction(X: DoubleArray, degree: Int): DoubleArray {
        val q = degree + 1
        val H = Array(X.size) { i -> DoubleArray(q) { j -> (i + 1.0).pow(j) } }

        val p = leastSquares(H, X)
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

    fun visualAttack(coverImage: BufferedImage, stegoImage: BufferedImage): BufferedImage {
        val width = coverImage.width
        val height = coverImage.height
        val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val coverPixel = coverImage.getRGB(x, y) and 0xFF
                val stegoPixel = stegoImage.getRGB(x, y) and 0xFF

                val difference = Math.abs(coverPixel - stegoPixel) * 10  // Усиление контраста
                val color = (difference shl 16) or (difference shl 8) or difference

                resultImage.setRGB(x, y, color)
            }
        }
        return resultImage
    }
}