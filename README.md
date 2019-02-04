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

When app starts, data is fetched from server using Retrofit (it's only done once or when it fails). Since the response is huge (>300.000 lines) I've opted to stream results in background using RxJava and buffer each 1000 lines to be inserted in database until the last line is fetched.

Also, since data is in CSV format, it was necessary to manually parse each line and extract values using some RegEx (thanks to this [great solution](https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes) to split only in commas outside quote marks).

About the database, I've ended up using regular `SQLiteDabase` and `SQLiteOpenHelper` classes because this exercise requires that results shouldn't be completely stored in the memory, so with these classes I could work with `Cursor` to extract data directly from database. Maybe a better solution could've be achieved by using the new [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/) component, but I still didn't use it.

When database is loaded, user can interact with data using search field to filter list. Again, I've used RxJava to take advantage of the _debounce_ feature where list is automatically refreshed while user types.

The search filter follow these rules:
- Input string is splitted by spaces (don't matter how many spaces are between words).
- If a term uses a valid postal code format (`####-###`), each part is searched in corresponding postal code column.
- If numeric terms are found, the first 2 is used as a full postal code and each one is searched in corresponding postal code column. The remaining numeric terms is searched in local name columm.
- if only one numeric term is found it'll be searched in both postal code columns.
- all other extracted terms is searched in local name column.
- all terms are searched together (using AND).

**OBS:** Accented characters aren't treated as regular ones by search in this release... :-(

On other hand, uppercase characters are ignored... :-D

About general architecture, I've tried to work with a **MVVM** pattern using a Repository to handle API and database manipulation while UI make all calls to its `ViewModel`. There are some depency injection with a `Injector` helper class.

### Exercise 2


The layout uses `FrameLayout`, so we can place `Views` on top of each other.

The `RecyclerView` has a custom adapter that generates 50 numbered itens.

A regular `ImageView` will work as a header for `RecyclerView` where it's placed above `RecyclerView` and under `Toolbar`. The image will be fetched from a provided URL using [Picasso](http://square.github.io/picasso/) library.

After `Toolbar` height is retrieved, the maximum scroll up will be equal to header height minus `Toolbar` height, so it'll be scrolled up until it's totally covered by `Toolbar`. And the maximum scroll down is up to its original position.

Also, the first item of list will have an offset equal to header height, so it won't be covered by it.

The `Toolbar` transparency is set proportionally by current header translation over maximum translation.

### Exercise 3

There are 3 different layouts for `RecyclerView` items with required `EditText's` behaviors (normal input, number only input and uppercase input) and proper `TextView` label.

The `RecyclerView` custom adapter generates 50 items and accordingly to item position it'll load one of these 3 layouts and keep the pattern: `normal input => number only input => uppercase input`.

For "uppercase" `EditText` it was required to set an `InputFilter.AllCaps()` in adapter to ensure all text input will be converted to uppercase.

Finally, the `RecyclerView` has a _touch listener_ to dismiss the soft keyboard when any area outside `EditText's` is touched.

### Exercise 4

Each flavor stores a property called `URL` on `app/build.gradle` that contains different URL's and they're read accodingly to flavor compilation and loaded to `WebView` when `Fragment` is created.

There's also a `string.xml` file for each flavor with different app names;