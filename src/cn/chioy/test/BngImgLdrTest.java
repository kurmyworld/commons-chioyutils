package cn.chioy.test;

import java.io.File;

import cn.chioy.img.BingImageLoader;

public class BngImgLdrTest {

	public static void main(String[] args) {
		BingImageLoader bngImgldr = new BingImageLoader();
		System.out.println(bngImgldr.getImgURL());
		File file = new File("d:/abc.jpg");
		bngImgldr.hasCache(file);
		bngImgldr.cacheTo(file);
		bngImgldr.hasCache(file);
	}
}
