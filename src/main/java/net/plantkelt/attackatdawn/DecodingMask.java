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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class DecodingMask {

	private static final String ALPHABET = "eeeeeeeeaaaaassssiiiittttnnnnrrruuullloooddccppmmvqfbghjxyzwk";

	private BufferedImage img, imgOverlap;
	private Font font;

	public DecodingMask(int width, int height, Font font, boolean uppercase,
			long seed, double density, double jitter, int overlap) {
		this.font = font;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		imgOverlap = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		this.fill(img, seed, uppercase, density, jitter, 0);
		this.fill(imgOverlap, seed, uppercase, density, jitter, overlap);
	}

	private void fill(BufferedImage img, long seed, boolean uppercase,
			double density, double disturb, int overlap) {
		String alphabet = ALPHABET;
		if (uppercase)
			alphabet = alphabet.toUpperCase();
		Random rand = new Random(seed);
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int dxy = (int) (fm.stringWidth("e") / Math.sqrt(density));
		int dr = (int) (dxy * disturb);

		int y = 0;
		while (y < img.getHeight() + dxy) {
			int x = 0;
			while (x < img.getWidth() + dxy) {
				char letter = alphabet.charAt(rand.nextInt(alphabet.length()));
				LetterMask lmask = new LetterMask(letter, font, fm,
						Color.BLACK);

				int x2 = x + (int) (dr * rand.nextDouble() - dr / 2);
				int y2 = y + (int) (dr * rand.nextDouble() - dr / 2);

				for (int dx = 0; dx <= overlap; dx++) {
					for (int dy = 0; dy <= overlap; dy++) {
						g2d.drawImage(lmask.getImage(), null,
								x2 + dx - lmask.getWidth() / 2,
								y2 + dy - lmask.getHeight() / 2);
					}
				}
				x += dxy;
			}
			y += dxy;
		}
	}

	public void save(OutputStream out) throws IOException {
		ImageIO.write(imgOverlap, "png", out);
	}

	public BufferedImage getImage(boolean overlap) {
		return overlap ? imgOverlap : img;
	}
}
