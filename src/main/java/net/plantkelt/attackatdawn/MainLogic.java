/*
    This file is part of Attack At Dawn.

    Attack At Dawn is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Attack At Dawn is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Attack At Dawn.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.plantkelt.attackatdawn;

import java.awt.Font;
import java.io.FileOutputStream;

import org.kohsuke.args4j.Option;

public class MainLogic {

	@Option(name = "--width", usage = "Grid width (pixesl). Default to 1024")
	public int width = 1024;
	@Option(name = "--height", usage = "Grid height (pixels). Default to 1024")
	public int height = 1024;
	@Option(name = "--fontName", handler = StringWithSpaceOptionHandler.class, usage = "Font name, such as 'Times New Roman'. Default to 'Serif'.")
	public String fontName = "Serif";
	@Option(name = "--fontSize", usage = "Font size (pixels). Default to 96.")
	public int fontSize = 96;
	@Option(name = "--seed", required = true, usage = "Grid seed. Each seed will result in a different and incompatible grid.")
	public long seed = 0L;
	@Option(name = "--message", handler = StringWithSpaceOptionHandler.class, usage = "Optional message to encode. Default to none.")
	public String message = null;
	@Option(name = "--maskFile", usage = "Decode mask filename. Default to 'mask'.")
	public String maskFile = "decode";
	@Option(name = "--codeFile", usage = "Encoded message filename. Default to 'code'.")
	public String codeFile = "encode";
	@Option(name = "--density", usage = "Letter density multiplier. Default to 1.0")
	public double density = 1.0;
	@Option(name = "--jitter", usage = "Letter random disturbation multiplier. Default to 1.0")
	public double jitter = 1.0;
	@Option(name = "--overlap", usage = "Decode mask overlap (pixels). Default to 0")
	public int overlap = 0;
	@Option(name = "--verticalSpacing", usage = "Spacing between baseline multiplier. Default to 1.0")
	public double verticalSpacing = 1.0;
	@Option(name = "--horizontalSpacing", usage = "Spacing between letters multiplier. Default to 1.0")
	public double horizontalSpacing = 1.0;
	@Option(name = "--horizontalStiffness", usage = "Encoded text horizontal stiffness. Default to 1.0")
	public double horizontalStiffness = 1.0;
	@Option(name = "--verticalStiffness", usage = "Encoded text vertical stiffness. Default to 1.0")
	public double verticalStiffness = 1.0;
	@Option(name = "--uppercase", usage = "Use uppercases as scrambling alphabet. Default to false (lowercases).")
	public boolean uppercase = false;
	@Option(name = "--bold", usage = "Use bold font. Default to false.")
	public boolean bold = false;
	@Option(name = "--italic", usage = "Use italic font. Default to false.")
	public boolean italic = false;
	@Option(name = "--debug", usage = "Output simulated decoded result to debug file. Default to false.")
	public boolean debug = false;

	public void run() throws Exception {

		Font font = new Font(fontName, (bold ? Font.BOLD : Font.PLAIN)
				| (italic ? Font.ITALIC : Font.PLAIN), fontSize);

		System.out.println("Generating decoding mask...");
		DecodingMask dmask = new DecodingMask(width, height, font, uppercase,
				seed, density, jitter, overlap);
		if (maskFile != null) {
			dmask.save(new FileOutputStream(String.format("%s.png", maskFile)));
		}

		int i = 0;
		while (message != null && !message.isEmpty()) {
			System.out.println(String
					.format("Generating encoded message sheet %d...", i + 1));
			EncodedMask emask = new EncodedMask(dmask, font);
			message = emask.write(message, horizontalSpacing, verticalSpacing,
					horizontalStiffness, verticalStiffness);
			if (codeFile != null) {
				emask.save(new FileOutputStream(
						String.format("%s%d.png", codeFile, i + 1)));
			}
			if (debug) {
				emask.saveDebug(new FileOutputStream(
						String.format("debug%d.png", i + 1)));
			}
			i++;
		}
	}
}
