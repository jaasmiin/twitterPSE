PDF=pdflatex
NAME=entwurf.tex

pdf:
	cd dia/ && dia -t svg *.dia && cd ..
	cd dia/ && sh createPDFs.sh && cd ..
	$(PDF) $(NAME)
	$(PDF) $(NAME)
	make clean

clean:
	rm -f *.log *.aux *.toc *.lof *.out *~ dia/*.pdf dia/*.svg
