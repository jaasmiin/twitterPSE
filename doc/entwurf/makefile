PDF=pdflatex
NAME=entwurf.tex

pdf:
	dia -e include/er.pdf include/er.dia
	$(PDF) $(NAME)
	$(PDF) $(NAME)
	make clean

clean:
	rm -f *.log *.aux *.toc *.lof *.out *~
