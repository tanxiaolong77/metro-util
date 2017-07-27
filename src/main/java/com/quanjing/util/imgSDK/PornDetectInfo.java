/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quanjing.util.imgSDK;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author jusisli
 */
public class PornDetectInfo {
		//result+"(0正常，1黄图，2疑似图片 )"
        private int result;
        // confidence+"(识别为黄图的置信度，范围0-100)"
        private double confidence;
        //pornScore+"(图片为色情图片的评分 )"
        private double pornScore;
        //normalScore+"(图片为正常图片的评分)"
        private double normalScore;
        //hotScore+"(图片为性感图片的评分 )"
        private double hotScore;
        
        
    public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		public double getConfidence() {
			return confidence;
		}
		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}
		public double getPornScore() {
			return pornScore;
		}
		public void setPornScore(double pornScore) {
			this.pornScore = pornScore;
		}
		public double getNormalScore() {
			return normalScore;
		}
		public void setNormalScore(double normalScore) {
			this.normalScore = normalScore;
		}
		public double getHotScore() {
			return hotScore;
		}
		public void setHotScore(double hotScore) {
			this.hotScore = hotScore;
		}
	public PornDetectInfo() {
    }
    public PornDetectInfo(int result, double confidence, double pornScore, double normalScore, double hotScore) {
			this.result = result;
			this.confidence = confidence;
			this.pornScore = pornScore;
			this.normalScore = normalScore;
			this.hotScore = hotScore;
	}


	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("Result =",getResult())
				.append("Confidence = ",getConfidence())
				.append("PornScore = ",getPornScore())
				.append("NormalScore = ",getNormalScore())
				.append("HotScore = ",getHotScore())
				.toString();
	}
}
