package com.example.testwork.captcha;


import com.vaadin.flow.component.html.Image;

import java.awt.image.BufferedImage;

// You can add your own implementation
public interface Captcha {
    BufferedImage getCaptchaBufferedImage();
    boolean checkUserAnswer(String userAnswer);
    Image getCaptchaImg();
}
