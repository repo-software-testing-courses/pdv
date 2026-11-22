package com.trackorjargh.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfWriter;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Lists;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;

@Controller
public class PdfCreate {

	public PdfCreate() {
	}
	
	public String createPdfLists(User user, List<Lists> listsUser) {
		String namePdf = "pdf-" + user.getName() + "-" + user.getId() + ".pdf";
		
		try {
			Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");
			if (!Files.exists(FILES_FOLDER)) {
				Files.createDirectories(FILES_FOLDER);
			}
			
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(new File(FILES_FOLDER.toFile(), namePdf)));
			document.open();
			Paragraph paragraph;
			
			document.add(new Paragraph("Listas del usuario " + user.getName()));
			document.add(new Paragraph(" "));
			
			for(Lists list:listsUser) {
				document.add(new Paragraph("La lista '" + list.getName() + "' contiene:"));
				document.add(new Paragraph(" "));
				
				paragraph = new Paragraph();
				paragraph.setTabSettings(new TabSettings(56f));
				paragraph.add(Chunk.TABBING);
				paragraph.add(new Chunk("Peliculas:"));	
				document.add(paragraph);				
				
				if(list.getFilms().size() == 0) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add("Aún sin contenido.");	
					document.add(paragraph);
				}
				
				for(Film f:list.getFilms()) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add(new Chunk("-" + f.getName()));	
					document.add(paragraph);
				}
				
				document.add(new Paragraph(" "));
				paragraph = new Paragraph();
				paragraph.setTabSettings(new TabSettings(56f));
				paragraph.add(Chunk.TABBING);
				paragraph.add(new Chunk("Series:"));	
				document.add(paragraph);
				
				if(list.getShows().size() == 0) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add("Aún sin contenido.");	
					document.add(paragraph);
				}
				
				for(Shows s:list.getShows()) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add(new Chunk("-" + s.getName()));	
					document.add(paragraph);
				}
				
				document.add(new Paragraph(" "));
				paragraph = new Paragraph();
				paragraph.setTabSettings(new TabSettings(56f));
				paragraph.add(Chunk.TABBING);
				paragraph.add(new Chunk("Libros:"));	
				document.add(paragraph);
				
				if(list.getBooks().size() == 0) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add("Aún sin contenido.");	
					document.add(paragraph);
				}
				
				for(Book b:list.getBooks()) {
					paragraph = new Paragraph();
					paragraph.setTabSettings(new TabSettings(98f));
					paragraph.add(Chunk.TABBING);
					paragraph.add(new Chunk("-" + b.getName()));	
					document.add(paragraph);
				}
			}
			
			document.close();
		} catch (Exception e) {
			return "/";
		}
		
		return "/crearpdf/listas";
	}
	
}
