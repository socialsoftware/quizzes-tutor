import re

patterns = [ ("\\\\\\\\emph\\{([\\s\\S]*?)\\}", "*\\1*")]

patterns += [("`", "'")]
patterns += [("\\\\\\\\textit\\{([\\s\\S]*?)\\}", "*\\1*")]
patterns += [("\\\\\\\\texttt\\{([\\s\\S]*?)\\}", "`\\1`")]
patterns += [("\\\\\\\\textbf\\{([\\s\\S]*?)\\}", "**\\1**")]
patterns += [("\\\\\\\\textsc\\{([\\s\\S]*?)\\}", "[\\1]\\{.smallcaps\\}")]
patterns += [("\\\\\\\\gp\\{\\}", "Graphite"), ("\\\\\\\\mw\\{\\}", "MediaWiki")]
patterns += [("\\\\\\\\ch\\{\\}", "Chrome"), ("\\\\\\\\gm\\{\\}", "GNU Mailman")]
patterns += [("\\\\\\\\ghc\\{\\}", "Glasgow Haskell Compiler")]
patterns += [("\\\\\\\\_", "_")]
patterns += [("\\\\\\\\%", "%")]
patterns += [("\\\\n", "")]
patterns += [("[ ]+", " ")]
patterns += [("[ ]*\\\\ewline[ ]*", "  \\\\n")]

patterns += [("[ ]*\\\\\\\\item([\\s\\S]*?)\\\\\\\\end\\{itemize\\}", "  \\\\n- \\1\\\\\\\\end{itemize}")]
patterns += [("[ ]*\\\\\\\\item([\\s\\S]*?)\\\\\\\\end\\{itemize\\}", "  \\\\n- \\1\\\\\\\\end{itemize}")]
patterns += [("[ ]*\\\\\\\\item([\\s\\S]*?)\\\\\\\\end\\{itemize\\}", "  \\\\n- \\1\\\\\\\\end{itemize}")]
patterns += [("[ ]*\\\\\\\\item([\\s\\S]*?)\\\\\\\\end\\{itemize\\}", "  \\\\n- \\1\\\\\\\\end{itemize}")]
patterns += [("[ ]*\\\\\\\\item([\\s\\S]*?)\\\\\\\\end\\{itemize\\}", "  \\\\n- \\1\\\\\\\\end{itemize}")]

patterns += [("[ ]*\\\\\\\\end{itemize}[ ]*", "  \\\\n"), ("[ ]*\\\\\\\\begin{itemize}", "")]
patterns += [("[ ]*\\\\t", ""), ("\\\\\\\\begin{center}[\\s]*\\\\\\\\includegraphics\\[.*]{.*}[\\s]*\\\\\\\\end{center}", "![image][image]")]

patterns += [("\\\\\\\\centering\\\\\\\\includegraphics\\[.*\\]{.*}", "![image][image]")]
patterns += [("[ ]*\\\\\\\\begin\\{quote\\}[ ]*([\\s\\S]*?)[ ]*\\\\\\\\end\\{quote\\}[ ]*", "  \\\\n>\"\\1\"  \\\\n")]
patterns += [(" \\\\\\\\begin\\{flushleft\\}", ""), (" \\\\\\\\end\\{flushleft\\}", "")]
patterns += [(" \\\\\\\\", "")]
patterns += [("\\\\\\\\ ", "")]
patterns += [("\\\\\\\\\\*", "\\*")]


with open ('dump/0_original.bak', 'r' ) as f:
    content = f.read()

for pattern in patterns:
	content = re.sub(pattern[0], pattern[1], content, flags = re.M)

with open('dump/1_withoutLateX.bak', "w") as f:
	f.write(content)