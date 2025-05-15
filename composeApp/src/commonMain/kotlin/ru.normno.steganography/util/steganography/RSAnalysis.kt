import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * RS analysis for a stego-image.
 * <P>
 * RS analysis is a system for detecting LSB steganography proposed by
 * Dr. Fridrich at Binghamton University, NY.  You can visit her
 * webpage for more information -
 * [://www.ws.binghamton.edu/fridrich/][http] <BR></BR>
 * Implemented as described in "Reliable detection of LSB steganography
 * in color and grayscale images" by J. Fridrich, M. Goljan and R. Du.
 * <BR></BR>
 * This code was produced with the aid of the authors and has been
 * verified as a correct implementation of RS Analysis.  Their assistance
 * has proved invaluable.
 *
 * @author Kathryn Hempstalk
</P> */
class RSAnalysis(val m: Int, val n: Int) : PixelBenchmark() {
    /**
     * The mask to be used for the pixel groups.
     */
    private val mMask: Array<IntArray?> = Array<IntArray?>(2) { IntArray(m * n) }

    /**
     * The x length of the mask.
     */
    private var mM = 0

    /**
     * The y length of the mask.
     */
    private var mN = 0

    //CONSTRUCTORS
    /**
     * Creates a new RS analysis with a given mask size of m x n.
     *
     * Each alternating bit is set to 1.  Eg for a mask of size 2x2
     * the resulting mask will be {1,0;0,1}.  Two masks are used - one is
     * the inverse of the other.
     *
     * @param m The x mask size.
     * @param n The y mask size.
     */
    init {


        //iterate through them and set alternating bits
        var k = 0
        for (i in 0..<n) {
            for (j in 0..<m) {
                if (((j % 2) == 0 && (i % 2) == 0)
                    || ((j % 2) == 1 && (i % 2) == 1)
                ) {
                    mMask[0]!![k] = 1
                    mMask[1]!![k] = 0
                } else {
                    mMask[0]!![k] = 0
                    mMask[1]!![k] = 1
                }
                k++
            }
        }


        //set up the mask size.
        mM = m
        mN = n
        //two masks
    }


    //FUNCTIONS
    /**
     * Does an RS analysis of a given image.
     * <P>
     * The analysis data returned is specified by name in
     * the getResultNames() method.
     *
     * @param image The image to analyse.
     * @param colour The colour to analyse.
     * @param overlap Whether the blocks should overlap or not.
     * @return The analysis information.
    </P> */
    fun doAnalysis(image: BufferedImage, colour: Int, overlap: Boolean): DoubleArray? {
        //get the images sizes

        val imgx = image.getWidth()
        val imgy = image.getHeight()

        var startx = 0
        var starty = 0
        var block: IntArray? = IntArray(mM * mN)
        var numregular = 0.0
        var numsingular = 0.0
        var numnegreg = 0.0
        var numnegsing = 0.0
        var numunusable = 0.0
        var numnegunusable = 0.0
        var variationB: Double
        var variationP: Double
        var variationN: Double

        while (startx < imgx && starty < imgy) {
            //this is done once for each mask...
            for (m in 0..1) {
                //get the block of data	
                var k = 0
                for (i in 0..<mN) {
                    for (j in 0..<mM) {
                        block!![k] = image.getRGB(startx + j, starty + i)
                        k++
                    }
                }


                //get the variation the block
                variationB = getVariation(block!!, colour)


                //now flip according to the mask
                block = flipBlock(block, mMask[m]!!)
                variationP = getVariation(block, colour)
                //flip it back
                block = flipBlock(block, mMask[m]!!)


                //negative mask
                mMask[m] = this.invertMask(mMask[m]!!)
                variationN = getNegativeVariation(block, colour, mMask[m]!!)
                mMask[m] = this.invertMask(mMask[m]!!)


                //now we need to work out which group each belongs to

                //positive groupings
                if (variationP > variationB) numregular++
                if (variationP < variationB) numsingular++
                if (variationP == variationB) numunusable++


                //negative mask groupings
                if (variationN > variationB) numnegreg++
                if (variationN < variationB) numnegsing++
                if (variationN == variationB) numnegunusable++


                //now we keep going...
            }
            //get the next position
            if (overlap) startx += 1
            else startx += mM

            if (startx >= (imgx - 1)) {
                startx = 0
                if (overlap) starty += 1
                else starty += mN
            }
            if (starty >= (imgy - 1)) break
        }


        //get all the details needed to derive x...
        val totalgroups = numregular + numsingular + numunusable
        val allpixels: DoubleArray? = this.getAllPixelFlips(image, colour, overlap)
        val x = getX(
            numregular, numnegreg, allpixels!![0], allpixels[2],
            numsingular, numnegsing, allpixels[1], allpixels[3]
        )


        //calculate the estimated percent of flipped pixels and message length
        val epf: Double
        val ml: Double
        if (2 * (x - 1) == 0.0) epf = 0.0
        else epf = abs(x / (2 * (x - 1)))

        if (x - 0.5 == 0.0) ml = 0.0
        else ml = abs(x / (x - 0.5))


        //now we have the number of regular and singular groups...
        val results: DoubleArray? = DoubleArray(28)


        //save them all...

        //these results
        results!![0] = numregular
        results[1] = numsingular
        results[2] = numnegreg
        results[3] = numnegsing
        results[4] = abs(numregular - numnegreg)
        results[5] = abs(numsingular - numnegsing)
        results[6] = (numregular / totalgroups) * 100
        results[7] = (numsingular / totalgroups) * 100
        results[8] = (numnegreg / totalgroups) * 100
        results[9] = (numnegsing / totalgroups) * 100
        results[10] = (results[4] / totalgroups) * 100
        results[11] = (results[5] / totalgroups) * 100


        //all pixel results
        results[12] = allpixels[0]
        results[13] = allpixels[1]
        results[14] = allpixels[2]
        results[15] = allpixels[3]
        results[16] = abs(allpixels[0] - allpixels[1])
        results[17] = abs(allpixels[2] - allpixels[3])
        results[18] = (allpixels[0] / totalgroups) * 100
        results[19] = (allpixels[1] / totalgroups) * 100
        results[20] = (allpixels[2] / totalgroups) * 100
        results[21] = (allpixels[3] / totalgroups) * 100
        results[22] = (results[16] / totalgroups) * 100
        results[23] = (results[17] / totalgroups) * 100


        //overall results
        results[24] = totalgroups
        results[25] = epf
        results[26] = ml
        results[27] = ((imgx * imgy * 3) * ml) / 8

        return results
    }

    /**
     * Gets the x value for the p=x(x/2) RS equation. See the paper for
     * more details.
     *
     * @param r The value of Rm(p/2).
     * @param rm The value of R-m(p/2).
     * @param r1 The value of Rm(1-p/2).
     * @param rm1 The value of R-m(1-p/2).
     * @param s The value of Sm(p/2).
     * @param sm The value of S-m(p/2).
     * @param s1 The value of Sm(1-p/2).
     * @param sm1 The value of S-m(1-p/2).
     * @return The value of x.
     */
    private fun getX(
        r: Double, rm: Double, r1: Double, rm1: Double,
        s: Double, sm: Double, s1: Double, sm1: Double
    ): Double {
        var x = 0.0 //the cross point.

        val dzero = r - s // d0 = Rm(p/2) - Sm(p/2)
        val dminuszero = rm - sm // d-0 = R-m(p/2) - S-m(p/2)
        val done = r1 - s1 // d1 = Rm(1-p/2) - Sm(1-p/2)
        val dminusone = rm1 - sm1 // d-1 = R-m(1-p/2) - S-m(1-p/2)


        //get x as the root of the equation 
        //2(d1 + d0)x^2 + (d-0 - d-1 - d1 - 3d0)x + d0 - d-0 = 0
        //x = (-b +or- sqrt(b^2-4ac))/2a
        //where ax^2 + bx + c = 0 and this is the form of the equation

        //thanks to a good friend in Dunedin, NZ for helping with maths
        //and to Miroslav Goljan's fantastic Matlab code
        val a = 2 * (done + dzero)
        val b = dminuszero - dminusone - done - (3 * dzero)
        val c = dzero - dminuszero

        if (a == 0.0)  //take it as a straight line
            x = c / b


        //take it as a curve
        val discriminant = b.pow(2.0) - (4 * a * c)

        if (discriminant >= 0) {
            val rootpos = ((-1 * b) + sqrt(discriminant)) / (2 * a)
            val rootneg = ((-1 * b) - sqrt(discriminant)) / (2 * a)


            //return the root with the smallest absolute value (as per paper)
            if (abs(rootpos) <= abs(rootneg)) x = rootpos
            else x = rootneg
        } else {
            //maybe it's not the curve we think (straight line)
            val cr = (rm - r) / (r1 - r + rm - rm1)
            val cs = (sm - s) / (s1 - s + sm - sm1)
            x = (cr + cs) / 2
        }

        if (x == 0.0) {
            val ar = ((rm1 - r1 + r - rm) + (rm - r) / x) / (x - 1)
            val `as` = ((sm1 - s1 + s - sm) + (sm - s) / x) / (x - 1)
            if ((`as` > 0) or (ar < 0)) {
                //let's assume straight lines again...
                val cr = (rm - r) / (r1 - r + rm - rm1)
                val cs = (sm - s) / (s1 - s + sm - sm1)
                x = (cr + cs) / 2
            }
        }
        return x
    }


    /**
     * Gets the RS analysis results for flipping performed on all
     * pixels.
     *
     * @param image The image to analyse.
     * @param colour The colour to analyse.
     * @param overlap Whether the blocks should overlap.
     * @return The analysis information for all flipped pixels.
     */
    private fun getAllPixelFlips(image: BufferedImage, colour: Int, overlap: Boolean): DoubleArray {
        //setup the mask for everything...

        val allmask = IntArray(mM * mN)
        for (i in allmask.indices) {
            allmask[i] = 1
        }


        //now do the same as the doAnalysis() method

        //get the images sizes
        val imgx = image.getWidth()
        val imgy = image.getHeight()

        var startx = 0
        var starty = 0
        var block: IntArray? = IntArray(mM * mN)
        var numregular = 0.0
        var numsingular = 0.0
        var numnegreg = 0.0
        var numnegsing = 0.0
        var numunusable = 0.0
        var numnegunusable = 0.0
        var variationB: Double
        var variationP: Double
        var variationN: Double

        while (startx < imgx && starty < imgy) {
            //done once for each mask
            for (m in 0..1) {
                //get the block of data
                var k = 0
                for (i in 0..<mN) {
                    for (j in 0..<mM) {
                        block!![k] = image.getRGB(startx + j, starty + i)
                        k++
                    }
                }


                //flip all the pixels in the block (NOTE: THIS IS WHAT'S DIFFERENT
                //TO THE OTHER doAnalysis() METHOD)
                block = flipBlock(block!!, allmask)


                //get the variation the block
                variationB = getVariation(block, colour)


                //now flip according to the mask
                block = flipBlock(block, mMask[m]!!)
                variationP = getVariation(block, colour)
                //flip it back
                block = flipBlock(block, mMask[m]!!)


                //negative mask
                mMask[m] = this.invertMask(mMask[m]!!)
                variationN = getNegativeVariation(block, colour, mMask[m]!!)
                mMask[m] = this.invertMask(mMask[m]!!)


                //now we need to work out which group each belongs to

                //positive groupings
                if (variationP > variationB) numregular++
                if (variationP < variationB) numsingular++
                if (variationP == variationB) numunusable++


                //negative mask groupings
                if (variationN > variationB) numnegreg++
                if (variationN < variationB) numnegsing++
                if (variationN == variationB) numnegunusable++


                //now we keep going...
            }
            //get the next position
            if (overlap) startx += 1
            else startx += mM

            if (startx >= (imgx - 1)) {
                startx = 0
                if (overlap) starty += 1
                else starty += mN
            }
            if (starty >= (imgy - 1)) break
        }


        //save all the results (same order as before)
        val results: DoubleArray? = DoubleArray(4)

        results!![0] = numregular
        results[1] = numsingular
        results[2] = numnegreg
        results[3] = numnegsing

        return results
    }


    val resultNames: List<String>
        /**
         * Returns an enumeration of all the result names.
         *
         * @return The names of all the results.
         */
        get() {
            val names = listOf(
                "Number of regular groups (positive)",
                "Number of singular groups (positive)",
                "Number of regular groups (negative)",
                "Number of singular groups (negative)",
                "Difference for regular groups",
                "Difference for singular groups",
                "Percentage of regular groups (positive)",
                "Percentage of singular groups (positive)",
                "Percentage of regular groups (negative)",
                "Percentage of singular groups (negative)",
                "Difference for regular groups %",
                "Difference for singular groups %",
                "Number of regular groups (positive for all flipped)",
                "Number of singular groups (positive for all flipped)",
                "Number of regular groups (negative for all flipped)",
                "Number of singular groups (negative for all flipped)",
                "Difference for regular groups (all flipped)",
                "Difference for singular groups (all flipped)",
                "Percentage of regular groups (positive for all flipped)",
                "Percentage of singular groups (positive for all flipped)",
                "Percentage of regular groups (negative for all flipped)",
                "Percentage of singular groups (negative for all flipped)",
                "Difference for regular groups (all flipped) %",
                "Difference for singular groups (all flipped) %",
                "Total number of groups",
                "Estimated percent of flipped pixels",
                "Estimated message length (in percent of pixels)(p)",
                "Estimated message length (in bytes)",
            )
            return names
        }


    /**
     * Gets the variation of the blocks of data. Uses
     * the formula f(x) = |x0 - x1| + |x1 + x3| + |x3 - x2| + |x2 - x0|;
     * However, if the block is not in the shape 2x2 or 4x1, this will be
     * applied as many times as the block can be broken up into 4 (without
     * overlaps).
     *
     * @param block The block of data (in 24 bit colour).
     * @param colour The colour to get the variation of.
     * @return The variation in the block.
     */
    private fun getVariation(block: IntArray, colour: Int): Double {
        var `var` = 0.0
        var colour1: Int
        var colour2: Int
        var i = 0
        while (i < block.size) {
            colour1 = getPixelColour(block[0 + i], colour)
            colour2 = getPixelColour(block[1 + i], colour)
            `var` += abs(colour1 - colour2).toDouble()
            colour1 = getPixelColour(block[3 + i], colour)
            colour2 = getPixelColour(block[2 + i], colour)
            `var` += abs(colour1 - colour2).toDouble()
            colour1 = getPixelColour(block[1 + i], colour)
            colour2 = getPixelColour(block[3 + i], colour)
            `var` += abs(colour1 - colour2).toDouble()
            colour1 = getPixelColour(block[2 + i], colour)
            colour2 = getPixelColour(block[0 + i], colour)
            `var` += abs(colour1 - colour2).toDouble()
            i = i + 4
        }
        return `var`
    }


    /**
     * Gets the negative variation of the blocks of data. Uses
     * the formula f(x) = |x0 - x1| + |x1 + x3| + |x3 - x2| + |x2 - x0|;
     * However, if the block is not in the shape 2x2 or 4x1, this will be
     * applied as many times as the block can be broken up into 4 (without
     * overlaps).
     *
     * @param block The block of data (in 24 bit colour).
     * @param colour The colour to get the variation of.
     * @param mask The negative mask.
     * @return The variation in the block.
     */
    private fun getNegativeVariation(block: IntArray, colour: Int, mask: IntArray): Double {
        var `var` = 0.0
        var colour1: Int
        var colour2: Int
        var i = 0
        while (i < block.size) {
            colour1 = getPixelColour(block[0 + i], colour)
            colour2 = getPixelColour(block[1 + i], colour)
            if (mask[0 + i] == -1) colour1 = invertLSB(colour1)
            if (mask[1 + i] == -1) colour2 = invertLSB(colour2)
            `var` += abs(colour1 - colour2).toDouble()

            colour1 = getPixelColour(block[1 + i], colour)
            colour2 = getPixelColour(block[3 + i], colour)
            if (mask[1 + i] == -1) colour1 = invertLSB(colour1)
            if (mask[3 + i] == -1) colour2 = invertLSB(colour2)
            `var` += abs(colour1 - colour2).toDouble()

            colour1 = getPixelColour(block[3 + i], colour)
            colour2 = getPixelColour(block[2 + i], colour)
            if (mask[3 + i] == -1) colour1 = invertLSB(colour1)
            if (mask[2 + i] == -1) colour2 = invertLSB(colour2)
            `var` += abs(colour1 - colour2).toDouble()

            colour1 = getPixelColour(block[2 + i], colour)
            colour2 = getPixelColour(block[0 + i], colour)
            if (mask[2 + i] == -1) colour1 = invertLSB(colour1)
            if (mask[0 + i] == -1) colour2 = invertLSB(colour2)
            `var` += abs(colour1 - colour2).toDouble()
            i = i + 4
        }
        return `var`
    }


    /**
     * Gets the given colour value for this pixel.
     *
     * @param pixel The pixel to get the colour of.
     * @param colour The colour to get.
     * @return The colour value of the given colour in the given pixel.
     */
    fun getPixelColour(pixel: Int, colour: Int): Int {
        if (colour == ANALYSIS_COLOUR_RED) return getRed(pixel)
        else if (colour == ANALYSIS_COLOUR_GREEN) return getGreen(pixel)
        else if (colour == ANALYSIS_COLOUR_BLUE) return getBlue(pixel)
        else return 0
    }


    /**
     * Flips a block of pixels.
     *
     * @param block The block to flip.
     * @param mask The mask to use for flipping.
     * @return The flipped block.
     */
    private fun flipBlock(block: IntArray, mask: IntArray): IntArray {
        //if the mask is true, negate every LSB
        for (i in block.indices) {
            if ((mask[i] == 1)) {
                //get the colour
                var red: Int = getRed(block[i])
                var green: Int = getGreen(block[i])
                var blue: Int = getBlue(block[i])


                //negate their LSBs
                red = negateLSB(red)
                green = negateLSB(green)
                blue = negateLSB(blue)


                //build a new pixel
                val newpixel = ((0xff shl 24) or ((red and 0xff) shl 16)
                        or ((green and 0xff) shl 8) or ((blue and 0xff)))


                //change the block pixel
                block[i] = newpixel
            } else if (mask[i] == -1) {
                //get the colour
                var red: Int = getRed(block[i])
                var green: Int = getGreen(block[i])
                var blue: Int = getBlue(block[i])


                //negate their LSBs
                red = invertLSB(red)
                green = invertLSB(green)
                blue = invertLSB(blue)


                //build a new pixel
                val newpixel = ((0xff shl 24) or ((red and 0xff) shl 16)
                        or ((green and 0xff) shl 8) or ((blue and 0xff)))


                //change the block pixel
                block[i] = newpixel
            }
        }
        return block
    }


    /**
     * Negates the LSB of a given byte (stored in an int).
     *
     * @param abyte The byte to negate the LSB of.
     * @return The byte with negated LSB.
     */
    private fun negateLSB(abyte: Int): Int {
        val temp = abyte and 0xfe
        if (temp == abyte) return abyte or 0x1
        else return temp
    }


    /**
     * Inverts the LSB of a given byte (stored in an int).
     *
     * @param abyte The byte to flip.
     * @return The byte with the flipped LSB.
     */
    private fun invertLSB(abyte: Int): Int {
        if (abyte == 255) return 256
        if (abyte == 256) return 255
        return (negateLSB(abyte + 1) - 1)
    }


    /**
     * Inverts a mask.
     *
     * @param mask The mask to invert.
     * @return The flipped mask.
     */
    private fun invertMask(mask: IntArray): IntArray {
        for (i in mask.indices) {
            mask[i] = mask[i] * -1
        }
        return mask
    }

    companion object {
        /**
         * A small main method that will print out the message length
         * in percent of pixels.
         *
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                println("Usage: invisibleinktoolkit.benchmark.RSAnalysis <imagefilename>")
                System.exit(1)
            }
            try {
                println("\nRS Analysis results")
                println("-------------------")
                val rsa = RSAnalysis(2, 2)
                val image = ImageIO.read(File(args[0]))
                var average = 0.0
                var results =
                    rsa.doAnalysis(image, ANALYSIS_COLOUR_RED, true)
                results?.let {
                    println("Result from red: " + results[26])
                    average += results[26]
                }
                results = rsa.doAnalysis(image, ANALYSIS_COLOUR_GREEN, true)
                results?.let {
                    println("Result from green: " + results[26])
                    average += results[26]
                }
                results = rsa.doAnalysis(image, ANALYSIS_COLOUR_BLUE, true)
                results?.let {
                    println("Result from blue: " + results[26])
                    average += results[26]
                }
                average = average / 3
                println("Average result: " + average)
                println()
            } catch (e: Exception) {
                println("ERROR: Cannot process that image type, please try another image.")
                e.printStackTrace()
            }
        }

        //VARIABLES
        /**
         * Denotes analysis to be done with red.
         */
        const val ANALYSIS_COLOUR_RED: Int = 0

        /**
         * Denotes analysis to be done with green.
         */
        const val ANALYSIS_COLOUR_GREEN: Int = 1

        /**
         * Denotes analysis to be done with blue.
         */
        const val ANALYSIS_COLOUR_BLUE: Int = 2
    }
} //end of class
