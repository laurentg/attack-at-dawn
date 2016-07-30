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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class LetterMask {

	private BufferedImage img;
	private Graphics2D g2d;

	public LetterMask(char letter, Font font, FontMetrics fm, Color color) {
		String txt = "" + letter;
		img = new BufferedImage(fm.stringWidth(txt), fm.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(color);
		g2d.setFont(font);
		g2d.drawString(txt, 0, fm.getAscent());
	}

	public int getWidth() {
		return img.getWidth();
	}

	public int getHeight() {
		return img.getHeight();
	}

	public BufferedImage getImage() {
		return img;
	}

	public double overlap(BufferedImage img2, int x, int y, Color color) {
		int downsampling = 4;
		int pixels = 0, overlap = 0;
		int argb = color.getRGB();
		for (int dx = 0; dx < img.getWidth(); dx += downsampling) {
			for (int dy = 0; dy < img.getHeight(); dy += downsampling) {
				int c1 = img.getRGB(dx, dy);
				if ((c1 & 0xFF000000) == 0)
					continue;
				pixels++;
				if (x + dx >= 0 && x + dx < img2.getWidth() && y + dy >= 0
						&& y + dy < img2.getHeight()) {
					int c2 = img2.getRGB(x + dx, y + dy);
					if (c2 != argb)
						continue;
				}
				overlap++;
			}
		}
		return 1.0 * overlap / pixels;
	}
}
