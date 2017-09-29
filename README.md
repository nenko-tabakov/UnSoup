# UnSoup

A simple and naive framework for that strange moment when you need to extract parts of html page to structured data.

## Sample usage 

The following HTML code 


```html 
<!DOCTYPE html> 
<html lang="en"> 
<head> 
</head> 

<body> 

<div id="main"> 
    <div class="brands"> 
        <a href="model/1" title="Abarth">Abarth</a> 
    </div> 
</div> 

</body> 
</html> 
``` 

can be mapped to this class 

```java 
public class Brand { 

    @ElementMapping(selector = ".brands a", attributeName = "href") 
    private String href; 

    @ElementMapping(selector = ".brands a", attributeName = "title") 
    private String title; 

    @ElementMapping(selector = ".brands a") 
    private String detailedTitle; 

    public String getHref() { 
        return href; 
    } 

    public String getTitle() { 
        return title; 
    } 
    
    public String getDetailedTitle() { 
        return detailedTitle; 
    } 
} 
``` 

by the following code 

```java 
UnSoup unsoup = new UnSoup(); 
Brand brand = unsoup.unsoup(new File("brands.html"), Brand.class); 
``` 

See the tests for more examples 

## Annotations (so far...) 

```html 

@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ElementMapping {
    String selector();

    Class<?> type() default String.class;

    String attributeName() default "";
}

``` 
