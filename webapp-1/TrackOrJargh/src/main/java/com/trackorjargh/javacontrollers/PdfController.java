package com.trackorjargh.javacontrollers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.ListsRepository;
import com.trackorjargh.pdf.PdfCreate;

@Controller
public class PdfController {
	@Autowired
	private UserComponent userComponent;
	@Autowired
	private PdfCreate pdfCreate;
	@Autowired
	private ListsRepository listsRepository;
	
	@RequestMapping("/crearpdflistas")
	public void handleFileDownloadPDF(HttpServletResponse res)
			throws FileNotFoundException, IOException {
		
		User user = userComponent.getLoggedUser();
		String namePdf = "pdf-" + user.getName() + "-" + user.getId() + ".pdf";
		
		pdfCreate.createPdfLists(userComponent.getLoggedUser(), listsRepository.findByUser(userComponent.getLoggedUser()));
		
		Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");
		Path pdf = FILES_FOLDER.resolve(namePdf);

		if (Files.exists(pdf)) {
			res.setContentType("application/pdf");
			res.setContentLength((int) pdf.toFile().length());
			FileCopyUtils.copy(Files.newInputStream(pdf), res.getOutputStream());

		} else {
			res.sendError(404);
		}
	}
}
