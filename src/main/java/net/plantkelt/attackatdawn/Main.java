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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println(String.format(
				"Attack at Dawn version %s - Secret messages made easy.",
				getVersion()));
		System.out.println(
				"Copyright (c) 2016 Laurent GRÉGOIRE <laurent.gregoire@protonmail.ch>");

		MainLogic logic = new MainLogic();
		CmdLineParser parser = new CmdLineParser(logic);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
		logic.run();
	}

	private static String getVersion() {
		Package thePackage = Main.class.getPackage();
		String implementationVersion = thePackage.getImplementationVersion();
		if (implementationVersion == null)
			return "(unknown)";
		return implementationVersion;
	}
}
