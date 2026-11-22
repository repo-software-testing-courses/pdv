package com.trackorjargh.commoncode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trackorjargh.javaclass.RandomGenerate;

@Service
public class CommonCodeImages {
	public String uploadImage(String imageName, MultipartFile file) {
		RandomGenerate generateRandomString = new RandomGenerate();
		String aux = generateRandomString.getRandomString(5);
		
		Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");
		String fileName = aux + "-image-" + imageName + ".jpg";

		if (!file.isEmpty()) {
			try {
				if (!Files.exists(FILES_FOLDER)) {
					Files.createDirectories(FILES_FOLDER);
				}

				File uploadedFile = new File(FILES_FOLDER.toFile(), fileName);
				file.transferTo(uploadedFile);

				return "/imagen/" + fileName;

			} catch (Exception e) {
				return "Error Upload";
			}
		} else {
			return "Empty File";
		}
	}
}
