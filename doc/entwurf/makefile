PDF=pdflatex
NAME=entwurf.tex

pdf:
	$(PDF) $(NAME)
	$(PDF) $(NAME)
	make clean
all:
	cd dia/ && dia -t svg *.dia && cd ..
	cd dia/ && sh createPDFs.sh && cd ..
	make pdf
clean:
	rm -f *.log *.aux *.toc *.lof *.out *~ dia/*.svg
