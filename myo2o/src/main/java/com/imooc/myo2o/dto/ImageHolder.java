package com.imooc.myo2o.dto;

import java.io.InputStream;

public class ImageHolder {
	private InputStream ImageInputStream;
	private String ImageName;

	public ImageHolder(InputStream imageInputStream, String imageName) {
		ImageInputStream = imageInputStream;
		ImageName = imageName;
	}

	public InputStream getImageInputStream() {
		return ImageInputStream;
	}

	public void setImageInputStream(InputStream imageInputStream) {
		ImageInputStream = imageInputStream;
	}

	public String getImageName() {
		return ImageName;
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

}
