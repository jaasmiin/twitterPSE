PDF=pdflatex
NAME=entwurf.tex

pdf:
	cd dia && dia -t pdf *.dia && cd ..
	$(PDF) $(NAME)
	$(PDF) $(NAME)
	make clean

clean:
	rm -f *.log *.aux *.toc *.lof *.out *~ dia/*.pdf
