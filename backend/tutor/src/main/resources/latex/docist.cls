%% 
%% Este ficheiro cria uma classe de \LaTeX2e adequada para a criacao
%% de teses  no IST, de acordo com as regras definidas nos
%% papeis fornecidos na secretaria de pos-graduados.
%%

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{sty/docist}[2003/11/17 The docist class]

\LoadClass[a4paper,11pt]{article}
\RequirePackage[portuges]{babel}
\RequirePackage[utf8]{inputenc}
\RequirePackage{t1enc}
\RequirePackage{setspace}
\RequirePackage[square,longnamesfirst]{natbib}
\RequirePackage{palatino}
%\RequirePackage{palatcm}
\RequirePackage{graphicx}

%\RequirePackage{multicol}



%\AtBeginDocument{\pagestyle{empty}}
%\AtBeginDocument{\pagestyle{empty}\begin{multicols}{2}[\maketitle]}
%\AtEndDocument{\end{multicols}}

\AtBeginDocument{\makecapa}

%%% Especificações dadas para as dimensões do artigo
%\setlength{\parindent}{0pt}
\setlength{\parskip}{2ex}

%%% Quero centrar o corpo do texto no papel.
%%% A largura e a altura do texto estão fixas, por isso tenho que
%%% calcular as margens...

\setlength{\textwidth}{150mm}
\setlength{\textheight}{240mm}

%% A margem esquerda e' calculada do seguinte modo
%%     (paperwidth - textwidth) / 2 - 1inch

\setlength{\oddsidemargin}{\paperwidth}
\addtolength{\oddsidemargin}{-\textwidth}
\setlength{\oddsidemargin}{0.5\oddsidemargin}
\addtolength{\oddsidemargin}{-1in}

\setlength{\evensidemargin}{\oddsidemargin}


%% O topo do texto tem que ficar a
%%    (paperheight - textheight) / 2 - 1inch
%%
%% No entanto, como ainda existe o header, temos que subtrair o
%% headheight e o headsep

\setlength{\topmargin}{\paperheight}
\addtolength{\topmargin}{-\textheight}
\setlength{\topmargin}{0.5\topmargin}
\addtolength{\topmargin}{-1in}
\addtolength{\topmargin}{-\headheight}
\addtolength{\topmargin}{-\headsep}



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Os elementos necessários para um doc...

% Titulo
\def\titulo#1{\gdef\@titulo{#1}}
\def\@titulo{\@latex@error{Falta o titulo do documento!}\@ehc}

% Autor
\def\autor#1{\gdef\@autor{#1}}
\def\@autor{\@latex@error{Falta o autor do documento!}\@ehc}


%% O comando a seguir e' uma copia do maketitle do report.cls

%% O logotipo da capa deve comecar a 57mm do topo
%% Por isso, vamos ter que usar um vspace com o seguinte valor
%%   57mm - 1in - \voffset - \headheight - \headsep
\newlength{\capaVLen}%
\setlength{\capaVLen}{55mm}%
\addtolength{\capaVLen}{-1in}%
\addtolength{\capaVLen}{-\voffset}%
\addtolength{\capaVLen}{-\headheight}%
\addtolength{\capaVLen}{-\headsep}%

%% Para alem disso, deve comecar a 24mm da margem esquerda do papel
%% Por isso, vamos ter que usar um hspace com o seguinte valor
%%   24mm - 1in - \hoffset - \oddsidemargin
\newlength{\capaHLen}%
\setlength{\capaHLen}{24mm}%
\addtolength{\capaHLen}{-1in}%
\addtolength{\capaHLen}{-\hoffset}%
\addtolength{\capaHLen}{-\oddsidemargin}%

\newcommand\makecapa{\par
  \begingroup
    \renewcommand\thefootnote{\@fnsymbol\c@footnote}%
    \def\@makefnmark{\rlap{\@textsuperscript{\normalfont\@thefnmark}}}%
    \long\def\@makefntext##1{\parindent 1em\noindent
            \hb@xt@1.8em{%
                \hss\@textsuperscript{\normalfont\@thefnmark}}##1}%
    \vspace*{\capaVLen}%
    \hspace*{\capaHLen}%
    \noindent\raisebox{0pt}[0pt][0pt]{\makebox[25mm]{\includegraphics[height=45mm]{sty/LogoIST-novo}}}
    \raisebox{0pt}[0pt][0pt]{\parbox[b]{.8\linewidth}{
        \centering{
          \LARGE\sffamily
          UNIVERSIDADE DE LISBOA\\
          INSTITUTO SUPERIOR TÉCNICO
        }
      }
    }
    \global\@topnum\z@   % Prevents figures from going at top of page.
    \@makecapa
    \thispagestyle{plain}\@thanks
  \endgroup
}
\def\@makecapa{%
  \null
  \vskip 2em%
  \begin{center}%
  \let \footnote \thanks
    {\LARGE \@title \par}%
    \vskip 1.5em%
    {\large
      \lineskip .5em%
      \begin{tabular}[t]{c}%
        \@author
      \end{tabular}\par}%
    \vskip 1em%
    {\large \@date}%
  \end{center}%
  \par
  \vskip 1.5em}


%%%Ends here
