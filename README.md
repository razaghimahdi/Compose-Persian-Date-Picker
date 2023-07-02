# Compose Persian Date Picker

`Compose Persian Date Picker` is a library that allows developers to add a Persian date picker to their Jetpack Compose apps. 
The library provides a customizable dialog that lets users select a date using the Persian calendar,
with options for updating the selected date and other settings.

## Give a Star! ⭐
If you like or are using this project to learn or start your solution, please give it a star. Thanks!


[![](https://jitpack.io/v/razaghimahdi/Compose-Persian-Date-Picker.svg)](https://jitpack.io/#razaghimahdi/Compose-Persian-Date-Picker)


## Quickstart

Here's a quick example of how to use the library:

1. Add the JitPack repository to your project-level build.gradle or settings.gradle file:

```groovy
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add the library dependency to your app-level build.gradle file:

```groovy
    dependencies {
            implementation 'com.github.razaghimahdi:Compose-Persian-Date-Picker:1.0.3'
    } 
```

3. Use the PersianDatePickerDialog in your app:

```kotlin

val rememberPersianDatePicker = rememberPersianDatePicker()
val showDialog = remember { mutableStateOf(false) }

if (showDialog.value) {
    PersianDatePickerDialog(
        rememberPersianDatePicker,
        Modifier.fillMaxWidth(),
        onDismissRequest = { showDialog.value = false },
        onDateChanged = { year, month, day ->
            // do something...
        })
}


Button(onClick = { showDialog.value = true }) {
    Text(text = "نمایش")
}
```

4. Customize the settings by calling methods on the rememberPersianDatePicker object:

```Kotlin
val rememberPersianDatePicker = rememberPersianDatePicker()

// 3 ways to update date
rememberPersianDatePicker.updateDate(date=Date())
rememberPersianDatePicker.updateDate(timestamp = Date().time)
rememberPersianDatePicker.updateDate(persianYear = 1401, persianMonth = 12, persianDay = 20)

rememberPersianDatePicker.updateMaxYear(1420)
rememberPersianDatePicker.updateMinYear(1350)

rememberPersianDatePicker.updateYearRange(10)
rememberPersianDatePicker.updateDisplayMonthNames(true)

PersianDatePickerDialog(
    rememberPersianDatePicker,
    Modifier.fillMaxWidth(),
    onDismissRequest = { showDialog.value = false },
    onDateChanged = { year, month, day ->
        // do something...
    }
)
```
## Example
For a more detailed example, check out the [example app](https://github.com/razaghimahdi/Compose-Persian-Date-Picker/blob/master/app/src/main/java/com/razaghimahdi/composepersiandatepicker/MainActivity.kt) included in the repository.

## Functions
rememberPersianDatePicker has these functions that can be useful:
| Function Name | Description |
| --- | --- |
| `getPersianYear()` | Returns the Persian year of the current date. |
| `getPersianMonth()` | Returns the Persian month (1-12) of the current date. |
| `getPersianDay()` | Returns the Persian day (1-31) of the current date. |
| `getGregorianYear()` | Returns the Gregorian year of the current date. |
| `getGregorianMonth()` | Returns the Gregorian month (1-12) of the current date. |
| `getGregorianDay()` |Returns the Gregorian day (1-31) of the current date. |
| `getDayOfWeek()` | Returns the day of the week (1-7) of the current date. |
| `getPersianMonthName()` | Returns the name of the Persian month of the current date (e.g. "اردیبهشت"). |
| `getPersianDayOfWeekName()` | Returns the name of the day of the week in Persian of the current date (e.g. "شنبه"). |
| `getPersianFullDate()` | Returns the full Persian date of the current date in the format "dayOfWeek day monthName year" (e.g. "پنج‌شنبه  10  شهریور  1401"). |
| `getGregorianDate()` | Returns the current date in the Gregorian calendar as a Date object. |
| `getTimestamp()` | Returns the Unix timestamp of the current date in milliseconds. |

## Screenshots
https://user-images.githubusercontent.com/61207818/220583893-ffcb39e2-5f34-4141-a81d-ddfb0b7339cf.mp4

## Extra Stuff

This library also includes a `NumberPicker` component that developers can use to add a number picker to their Jetpack Compose apps. 
The `NumberPicker` component is complementary to the `PersianDatePickerDialog` and provides a way to easily select a number value in the app.

```Kotlin

var selectedValue by remember { mutableStateOf(0) }

NumberPicker(
    value = selectedValue,
    range = 0..10,
    onValueChange = {
        selectedValue = it
    }
)


```
## Contributing
Contributions are welcome! If you find a bug or would like to create a new feature, please submit a pull request.

## License
This library is licensed under the MIT License. See [LICENSE.txt](https://github.com/razaghimahdi/Compose-Persian-Date)

Developed by Mahdi Razzaghi Ghaleh
