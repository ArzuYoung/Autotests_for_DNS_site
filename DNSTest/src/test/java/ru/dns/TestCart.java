package ru.dns;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class TestCart {
    public ChromeDriver driver;
    public String testPageURL = "https://www.dns-shop.ru/";
    private String expectedCartIndexAfterAddItem = "1";

    /**
     * Настройка драйвера, ожиданий и запуск страницы
     */
    @Before
    public void SetUpSettings()
    {
        System.setProperty("webdriver.chrome.driver", "ChromeDriver/chromedriver_win32/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Long.parseLong("5"), TimeUnit.SECONDS);
        driver.get(testPageURL);
    }

    /**
     * Тестирование добавления товара в корзину
     */
    @Test
    public void AddToCartTest()
    {
        //добавляем товар в корзину
        AddItemToCart();

        //переходим в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        //проверяем, что добавление прошло успешно
        //проверяем, что счетчик товаров в корзине изменился с 0 на 1
        WebElement cartIndexElement = driver.findElementByCssSelector(".cart-link__badge");
        String cartIndex = cartIndexElement.getText();

        String message1 = String.format("Получили: %s, Ожидалось: %s", cartIndex, expectedCartIndexAfterAddItem);
        Assert.assertEquals(message1, expectedCartIndexAfterAddItem, cartIndex);

        //в конце удаляем товар из корзины
        DeleteItemFromCart();
    }

    /**
     * Проверка соответствия цены корзины вверху страницы цене товара в ней
     */
    @Test
    public void CheckCartPriceAfterAddFirstItem()
    {
        //Добавляем товар в корзину
        AddItemToCart();

        //Переходим в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        //Проверим, что цена корзины стала равна цене добавленного товара
        WebElement cartPriceElement = driver.findElementByCssSelector(".buttons__link-price");
        String cartPrice = cartPriceElement.getText();
        int priceLenght = cartPrice.length();
        WebElement itemPriceElement = driver.findElementByCssSelector(".cart-items__product-price .price__current");
        String itemPrice = itemPriceElement.getText().substring(0, priceLenght);

        String message2 = String.format("Стоимость товара: %s, Стоимость корзины: %s, Ожидается совпадение", itemPrice, cartPrice);
        Assert.assertEquals(message2, cartPrice, itemPrice);

        //Удаляем товар из корзины
        DeleteItemFromCart();
    }

    /**
     * Проверка того, что корзина пуста
     */
    @Test
    public void CheckCartIsEmpty(){
        //проверяем, что счетчик товаров в корзине не отображается на экране
        WebElement cartIndexElement = driver.findElementByCssSelector(".cart-link__badge");
        Boolean cartIndexIsDisplayed = cartIndexElement.isDisplayed();
        String message = "В избранном есть лишние товары";
        Assert.assertFalse(message, cartIndexIsDisplayed);
    }

    /**
     * Тестирование удаления товара из корзины
     */
    @Test
    public void DeleteFromCartTest()
    {
        //Добавляем товар
        AddItemToCart();

        //Удаляем элемент из корзины
        DeleteItemFromCart();

        //проверяем, счетчика товаров нет на странице
        Boolean elementCondition;
        try{
            elementCondition = driver.findElementByCssSelector(".cart-link__badge").isDisplayed();
        }
        catch (NoSuchElementException e){
            elementCondition = false;
        }

        String message1 = "В корзине есть товары";
        Assert.assertFalse(message1, elementCondition);

    }

    public void AddItemToCart()
    {
        //вводим товар в поиск
        driver.findElement(By.xpath("(//input[@name='q'])[2]")).sendKeys("фотоаппарат");

        //кликаем на поиск
        driver.findElementByXPath("//div[2]/span[2]").click();

        //кликаем на первый товар в выборке, переходим на карточку товара
        driver.findElementByCssSelector(".catalog-product:nth-child(1) > .catalog-product__name").click();

        //добавляем товар в корзину
        driver.findElementByXPath("//button[contains(.,'Купить')]").click();

        //ждем пока товар добавится
        WebDriverWait wait = new WebDriverWait(driver, '5');
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'В корзине')]")));

    }

    public void DeleteItemFromCart()
    {
        //Переходим в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        //Нажимаем на "Удалить" у карточки товара в корзине
        driver.findElementByCssSelector(".remove-button__title").click();

        //ждем пока товар удалится
        WebDriverWait wait = new WebDriverWait(driver, '5');
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class, 'empty-message__title-empty-cart')]")));

    }

    /**
     * Закрытие страницы
     */
    @After
    public void closePage()
    {
        driver.quit();
    }

}
