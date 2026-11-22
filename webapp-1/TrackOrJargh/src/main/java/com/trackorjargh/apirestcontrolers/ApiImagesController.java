package com.trackorjargh.apirestcontrolers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trackorjargh.commoncode.CommonCodeImages;

@RestController
@RequestMapping("/api")
public class ApiImagesController {
	private final CommonCodeImages commonCodeImages;
	
	@Autowired
	public ApiImagesController(CommonCodeImages commonCodeImages) {
		this.commonCodeImages = commonCodeImages;
	}

	@RequestMapping(value = "/subirimagen", method = RequestMethod.POST, headers = { "content-type=multipart/mixed",
			"content-type=multipart/form-data" })
	public String putUpload(MultipartFile image) {
		return commonCodeImages.uploadImage(
				image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.')), image);
	}
}
