#  SC Android Challenge
By Márcio Souza de Oliveira

## The Challenge


It is intended that the `WTest` app be implemented.

The app should use _Bottom Navigation_ (bottom tabs) with each exercise on its own tab.

With the code, an explanation of what has been done and relevant notes should be given; either by comments to the code or report (can be a .txt, nothing much worked).

### Exercise 1

The app should collect a list of postal codes and present it in a `RecyclerView`. The list should be searchable.
- Each entry should be in the format `####-###, <Name>`.
- The app should collect, and interpret, the git list (https://github.com/centraldedados/codigos_postais) via an HTTP GET.
- The app should store the required data in a database; that is, the download should only be done the first time the app is running (except when the app is finished before download full).
- Database results must not be completely stored in the memory; that is, you should not use `ArrayList`, `Set`, etc. to check the postal codes. This should be made directly to the database.
- The search field must be an `EditText` and always be presented on the screen. Besides that,
no `RecyclerView` entry should be behind the keyboard.
- The search can not block the UI thread.
- It should be possible to search by code or name, not having to enter the name by
all. The results of the following searches should contain `"2695-650, São João da Talha"`:
  - "2695"
  - "São João"
  - "sAo joA da TaLH"
  - "sao talha"
  - "talh joa"
  - "joao talha"

### Exercise 2

The app should display a list of 50 items. `RecyclerView` should display a header
which consists of an asynchronously collected image.
- The header should be in the `2:1` format.
- The image should occupy the full width of the screen and be collected by HTTP.
- When scrolling down to the `RecyclerView`, the top of the image should remain fixed to the top the screen; scroll up must maintain the native behavior.
- The App bar should be transparent and opaque, gradually scrolling up. Add title to see the behavior; when transparent, white title; when opaque, bar the white and black title.

### Exercise 3

The app should present a `RecyclerView` with 50 text fields.
- Each row should have a `TextView` and an `EditText`.
- When touching outside an `EditText`, hide the keyboard.
- No `RecyclViewView` entries should be behind the keyboard.
- The list should repeat the following sequence:
  - plain text
  - numbers
  - force caps

### Exercise 4

The app should display a website, set up in the Gradle file.
- The app project should have two flavors.
- Each flavor should display a different website and have its own app name

## The Solution

### Exercise 1

To be implemented

### Exercise 2

To be implemented

### Exercise 3

There are 3 different layouts for `RecyclerView` items with required `EditText's` behaviors (normal input, number only input and uppercase input) and proper `TextView` label.

The `RecyclerView` custom adapter generates 50 items and accordingly to item position it'll load one of these 3 layouts and keep the pattern: `normal input => number only input => uppercase input`.

For "uppercase" `EditText` it was required to set an `InputFilter.AllCaps()` in adapter to ensure all text input will be converted to uppercase.

Finally, the `RecyclerView` has a _touch listener_ to dismiss the soft keyboard when any area outside `EditText's` is touched.

### Exercise 4

Each flavor stores a property called `URL` on `app/build.gradle` that contains different URL's and they're read accodingly to flavor compilation and loaded to `WebView` when `Fragment` is created.

There's also a `string.xml` file for each flavor with different app names;