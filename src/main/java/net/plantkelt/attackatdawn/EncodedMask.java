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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class EncodedMask {

	private DecodingMask decodingMask;
	private BufferedImage img;
	private Font font;

	public EncodedMask(DecodingMask decodingMask, Font font) {
		this.decodingMask = decodingMask;
		this.font = font;

		/* Create an image the same size as the decoding mask */
		BufferedImage dimg = decodingMask.getImage(false);
		img = new BufferedImage(dimg.getWidth(), dimg.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		g2d.drawImage(dimg, null, 0, 0);

		/* Invert the image */
		short[] data = new short[256];
		for (short i = 0; i < 256; i++) {
			data[i] = (short) (255 - i);
		}
		LookupTable lookupTable = new ShortLookupTable(0, data);
		LookupOp op = new LookupOp(lookupTable, null);
		img = op.filter(img, null);
	}

	public String write(String text, double horizontalSpacing,
			double verticalSpacing, double horizontalStiffness,
			double verticalStiffness) {
		Graphics2D g2d = img.createGraphics();
		FontMetrics fm = g2d.getFontMetrics(font);
		int dxy = fm.stringWidth("e");

		int downsampling = 1;
		int baseline = (int) (dxy * 2 * verticalSpacing);

		int x = 0, y = 0;
		String retval = null;
		int lastDy = 0;
		for (int ci = 0; ci < text.length(); ci++) {
			char letter = text.charAt(ci);
			System.out.print(letter);
			double minOverlap = Double.MAX_VALUE;
			int bestDx = 0, bestDy = 0;
			LetterMask lmask = new LetterMask(letter, font, fm, Color.WHITE);
			for (int dx = 0; dx < dxy * 2 && x + dx + lmask.getWidth() < img
					.getWidth(); dx += downsampling) {
				for (int dy = -baseline / 3; dy < baseline
						/ 3; dy += downsampling) {
					double overlap = lmask.overlap(img, x + dx, y + dy,
							Color.WHITE);
					double overlap2 = overlap
							+ 0.02 * verticalStiffness * dy * dy / dxy / dxy
							+ 0.02 * verticalStiffness * (dy - lastDy)
									* (dy - lastDy) / dxy / dxy
							+ 0.02 * horizontalStiffness * dx * dx / dxy / dxy;
					if (overlap2 < minOverlap) {
						minOverlap = overlap2;
						bestDx = dx;
						bestDy = dy;
					}
				}
			}
			g2d.drawImage(lmask.getImage(), null, x + bestDx, y + bestDy);
			x += bestDx + (int) (lmask.getWidth() * horizontalSpacing);
			lastDy = bestDy;
			if (letter == ' ')
				x += dxy;
			if (x + dxy > img.getWidth()) {
				x = 0;
				y += baseline;
				lastDy = 0;
			}
			if (y + dxy * 2 > img.getHeight()) {
				if (ci < text.length() - 1) {
					retval = text.substring(ci + 1);
				}
				break;
			}
		}
		y += fm.getHeight();
		if (y < img.getHeight()) {
			// Trim vertically
			BufferedImage img2 = new BufferedImage(img.getWidth(), y,
					BufferedImage.TYPE_INT_RGB);
			g2d = img2.createGraphics();
			g2d.drawImage(img, null, 0, 0);
			img = img2;
		}
		System.out.println(String.format(" - %d characters remaining.",
				retval == null ? 0 : retval.length()));
		return retval;
	}

	public void save(OutputStream out) throws IOException {
		ImageIO.write(img, "png", out);
	}

	public void saveDebug(OutputStream out) throws IOException {
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(decodingMask.getImage(true), null, 0, 0);
		ImageIO.write(img, "png", out);
	}
}
