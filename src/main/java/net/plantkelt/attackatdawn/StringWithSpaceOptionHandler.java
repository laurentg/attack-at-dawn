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
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class StringWithSpaceOptionHandler extends OptionHandler<String> {

	public StringWithSpaceOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<String> setter) {
		super(parser, option, setter);
	}

	@Override
	public String getDefaultMetaVariable() {
		return Messages.DEFAULT_META_STRING_ARRAY_OPTION_HANDLER.format();
	}

	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		int counter = 0;
		StringBuffer sb = new StringBuffer();
		for (; counter < params.size(); counter++) {
			String param = params.getParameter(counter);

			if (param.startsWith("-")) {
				break;
			}

			sb.append(param).append(" ");
		}

		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);

		setter.addValue(sb.length() == 0 ? null : sb.toString());

		return counter;
	}
}
