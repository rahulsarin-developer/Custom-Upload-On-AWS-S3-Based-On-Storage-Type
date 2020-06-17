package com.springboot.poc.documentupload.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.poc.documentupload.utility.FileUploadUtility;
import com.springboot.poc.documentupload.utility.S3Utility;

@RestController
@RequestMapping("/api/v1/aws")
public class MstDocumentController {
	
	@Autowired
	private S3Utility s3Utility;
	
	@Autowired
	private FileUploadUtility fileUploadUtility;

	@PostMapping("uploads")
	public Map<String, Object> uploads(@RequestParam List<MultipartFile> file, @RequestParam Integer storageType) throws IOException {
		Map<String, MultipartFile> maps = new HashMap<>();
		List<String> names = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		if(storageType.equals(1)) {
			putIntoDataBase(file, maps, names);
			s3Utility.uploadOnS3All(maps);
			map.put("status","Uploaded On S3 Private");
		} else if(storageType.equals(2)) {
			putIntoDataBase(file, maps, names);
			fileUploadUtility.uploadOnFileSystem(maps);
			map.put("status","Uploaded On File System");
		}
		else if(storageType.equals(3)) {
			putIntoDataBase(file, maps, names);
			s3Utility.uploadOnS3All(maps);
			fileUploadUtility.uploadOnFileSystem(maps);
			map.put("status","Uploaded On S3 and File System");
		}
		else {
			map.put("storageType","No File Uploaded");
		}
		map.put("fileNames", names);
		map.put("size", file.size());
		return map;
	}

	private void putIntoDataBase(List<MultipartFile> file, Map<String, MultipartFile> maps, List<String> names) {
		file.forEach(e -> {
			String fileName = UUID.randomUUID() + e.getOriginalFilename();
			maps.put(fileName, e);
			names.add(fileName);
		});
	}
}

