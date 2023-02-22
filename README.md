# Compose-Persian-Date-Picker

A library which allows you to have Persian date picker dialog by **Jetpack Compose**.

[![](https://jitpack.io/v/razaghimahdi/Compose-Persian-Date-Picker.svg)](https://jitpack.io/#razaghimahdi/Compose-Persian-Date-Picker)

### Step 1. Add it in your project-level `build.gradle` or `settings.gradle` file:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

### Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.razaghimahdi:Compose-Persian-Date-Picker:1.0.0'
	} 

### Step 3. How to use

```kotlin

val rememberPersianDataPicker = rememberPersianDataPicker()
val showDialog = remember { mutableStateOf(false) }

if (showDialog.value) {
    PersianDataPickerDialog(
        rememberPersianDataPicker,
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

### Step 4. How to initial

```Kotlin
val rememberPersianDataPicker = rememberPersianDataPicker()


// 3 ways to update date
rememberPersianDataPicker.updateDate(date=Date())
rememberPersianDataPicker.updateDate(timestamp = Date().time)
rememberPersianDataPicker.updateDate(persianYear = 1401, persianMonth = 12, persianDay = 20)

rememberPersianDataPicker.updateSelectedYear(1400)
rememberPersianDataPicker.updateSelectedDay(10)
rememberPersianDataPicker.updateSelectedMonth(5)

rememberPersianDataPicker.updateMaxYear(1420)
rememberPersianDataPicker.updateMinYear(1350)

rememberPersianDataPicker.updateYearRange(10)
rememberPersianDataPicker.updateDisplayMonthNames(true)


PersianDataPickerDialog(
    rememberPersianDataPicker,
    Modifier.fillMaxWidth(),
    onDismissRequest = { showDialog.value = false },
    onDateChanged = { year, month, day ->
        // do something...
    })

```


Developed by Mahdi Razzaghi Ghaleh
