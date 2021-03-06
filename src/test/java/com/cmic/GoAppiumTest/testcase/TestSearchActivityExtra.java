package com.cmic.GoAppiumTest.testcase;

import static org.testng.Assert.assertEquals;


import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.cmic.GoAppiumTest.App;
import com.cmic.GoAppiumTest.base.BaseTest;
import com.cmic.GoAppiumTest.helper.Tips;
import com.cmic.GoAppiumTest.testcase.retry.FailRetry;
import com.cmic.GoAppiumTest.util.ContextUtil;
import com.cmic.GoAppiumTest.util.ElementUtil;
import com.cmic.GoAppiumTest.util.KeyboardUtil;
import com.cmic.GoAppiumTest.util.LogUtil;
import com.cmic.GoAppiumTest.util.PageRouteUtil;
import com.cmic.GoAppiumTest.util.ScreenUtil;
import com.cmic.GoAppiumTest.util.WaitUtil;

import io.appium.java_client.android.AndroidElement;

/**
 * 包含搜索页面的一些非常规测试，包括联想和搜索结果，这两者结合起来测试比较方便，故放在一起
 * 
 * @风险 0403发现AutoCompleteTextView无法定位，考虑使用坐标定位，及其不稳定
 * @author kiwi
 */
public class TestSearchActivityExtra extends BaseTest {

	private String searchHistoryItem;

	@Tips(description = "搜索联想功能测试", triggerTime = "假设已经在SearchAct", riskPoint = "耦合度暂不考虑，从MainTest完成进入")
	@Override
	public void setUpBeforeClass() {

	}

	@Override
	public void tearDownAfterClass() {

	}

	@Test(retryAnalyzer = FailRetry.class)
	public void initCheck() throws InterruptedException {// 1
		// TODO 后期需要确定是否为初次安装还是应用启动
		// 先确认是否进入该页面
		System.err.println("进行[" + getClass().getSimpleName() + "]用例集的初始化检验，失败则跳过该用例集的所有测试");
		WaitUtil.forceWait(2);
		assertEquals(ContextUtil.getCurrentActivity(), ".activity.SearchActivity");
		ScreenUtil.screenShot("进入必备应用搜索界面-无搜索历史");
	}

	@Test(dependsOnMethods = { "initCheck" })
	@Tips(description = "热搜联想")
	public void checkSearchRalation() throws InterruptedException {
		LogUtil.printCurrentMethodName();
		WaitUtil.forceWait(2);
		AndroidElement et = mDriver.findElement(By.id("com.cmic.mmnes:id/searchText"));
		et.click();// 点击出现搜索联想
		ScreenUtil.screenShot("联想搜索前...");
		et.sendKeys("移动");
		WaitUtil.forceWait(2);
		ScreenUtil.screenShot("联想搜索后...");
	}

	@Test(dependsOnMethods = { "initCheck" }, timeOut = 15000)
	@Tips(description = "点击下载", //
			riskPoint = "由于自动补全控件无法定位，当前预期先使用坐标定位，待解决。不抛出异常，只做正向验证//" + "待补充Robotium白盒测试")
	public void checkClickDownload() {
		LogUtil.printCurrentMethodName();
		// TODO 当前不实现
		// 点击下载按钮
		// 0402当前只能使用这种效果较差的方法
	}

	@Test(dependsOnMethods = { "checkSearchRalation" })
	@Tips(description = "点击直达的联想条目", riskPoint = "联想结果不确定，不一定能点中")
	public void checkClick2DetailByDirectItem() throws InterruptedException {
		LogUtil.printCurrentMethodName();
		ScreenUtil.screenShot("联想搜索后点击直达条目前...");
		// 1.进行点击
		int targetXPx = ScreenUtil.getDeviceWidth() / 2;
		int targetYPx = ScreenUtil.getStatusBarHeight() + ScreenUtil.getActionBarHeight()
				+ ScreenUtil.dp2Px(App.RELATION_DIRECTION_ITEM_HEIGHT_DP) / 2;
		System.err.println(targetXPx + " " + targetYPx);
		ScreenUtil.singleTap(targetXPx, targetYPx);
		WaitUtil.forceWait(2);
		ScreenUtil.screenShot("联想搜索后点击直达条目前...");
		// 预期
		if (ContextUtil.getCurrentActivity().equals(".activity.DetailActivity")) {
			PageRouteUtil.pressBack();
		} else if (ElementUtil.isElementPresent(By.id("com.cmic.mmnes:id/pager_indicator"))) {
			PageRouteUtil.pressBack();
		} else {// 没有点中
		}
		WaitUtil.forceWait(2);
	}

	@Test(dependsOnMethods = { "checkClickDownload" })
	@Tips(description = "点击简单的联想条目", riskPoint = "联想结果不确定，不一定能点中")
	public void checkClick2SearchResultByEasyItem() throws InterruptedException {
		WaitUtil.implicitlyWait(5);
		LogUtil.printCurrentMethodName();
		// 0.前置操作,显示搜索联想
		AndroidElement et = mDriver.findElement(By.id("com.cmic.mmnes:id/searchText"));
		et.clear();
		et.click();// 点击出现搜索联想
		et.sendKeys("移动");
		WaitUtil.forceWait(2);
		KeyboardUtil.hideKeyBoard();
		ScreenUtil.screenShot("联想搜索后点击简单条目前...");
		// 1.进行点击
		int targetXPx = ScreenUtil.getDeviceWidth() / 2;
		int targetYPx = ScreenUtil.getStatusBarHeight() + ScreenUtil.getActionBarHeight()
				+ ScreenUtil.dp2Px(App.RELATION_DIRECTION_ITEM_HEIGHT_DP * 2 + 10);
		System.err.println(targetXPx + " " + targetYPx);
		ScreenUtil.singleTap(targetXPx, targetYPx);
		WaitUtil.forceWait(2);
		ScreenUtil.screenShot("联想搜索后点击简单条目后...");
		// 预期
		if (ContextUtil.getCurrentActivity().equals(".activity.DetailActivity")) {
			PageRouteUtil.pressBack();
		} else if (ElementUtil.isElementPresent(By.id("com.cmic.mmnes:id/pager_indicator"))) {
			AndroidElement searchEt = mDriver.findElement(By.id("com.cmic.mmnes:id/searchText"));
			searchHistoryItem = searchEt.getText();
			PageRouteUtil.pressBack();
		} else {// 没有点中

		}
	}

	@Test(dependsOnMethods = { "checkClick2SearchResultByEasyItem" })
	public void checkSearchHistory() {
		LogUtil.printCurrentMethodName();
		ScreenUtil.screenShot("搜索历史生成");
	}

	@Test(dependsOnMethods = { "checkClick2SearchResultByEasyItem" })
	public void checkEnterDetailFromHistory() throws InterruptedException {
		WaitUtil.implicitlyWait(5);
		LogUtil.printCurrentMethodName();
		if (searchHistoryItem == null) {
			AndroidElement et = mDriver.findElement(By.id("com.cmic.mmnes:id/searchText"));
			et.click();// 点击出现搜索联想
			et.sendKeys(searchHistoryItem = "移动");
			WaitUtil.forceWait(2);
		}
		String itemHistoryUiSeletor = "new UiSelector().className(\"android.widget.TextView\").textContains(\""
				+ searchHistoryItem + "\")";
		AndroidElement item = mDriver.findElementByAndroidUIAutomator(itemHistoryUiSeletor);
		item.click();
		WaitUtil.forceWait(2);
		ScreenUtil.screenShot("点击搜索历史");
		PageRouteUtil.pressBack();
		WaitUtil.forceWait(2);
	}

	@Test(dependsOnMethods = { "checkClick2SearchResultByEasyItem" })
	public void checkClearHistory() {
		LogUtil.printCurrentMethodName();
		ScreenUtil.screenShot("点击清除历史前");
		if (ElementUtil
				.isElementPresent("new UiSelector().className(\"android.widget.TextView\").textContains(\"清除\")")) {
			AndroidElement clearHistoryIcon = mDriver.findElementByAndroidUIAutomator(
					" new UiSelector().className(\"android.widget.TextView\").textContains(\"清除\")");
			clearHistoryIcon.click();
			ScreenUtil.screenShot("点击清除历史后");
			boolean isVisiable = ElementUtil
					.isElementPresent("new UiSelector().className(\"android.widget.TextView\").textContains(\"搜索历史\")");
			assertEquals(isVisiable, false);
		}
	}
}
