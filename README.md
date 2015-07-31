AdvancedRecyclerView
====================

This library for Android provides advanced functionality for Google's RecyclerView.

**Attention!** This is a development version, may be unstable and should not be used in production.

Features included:

- EmptyView if adapter is empty (provided by the AdvancedRecyclerView itself or by the connected Adapter)
- Floating headers at the top of the scroll area.
- Support of ```wrap_content``` when using the LinearLayoutManager or GridLayoutManager of this library.
- Support of OverScrollMode ```ifContentScrolls```.
- Padding implemented in LayoutManager, so the edge effect is not affected by padding.

## Download

gradle:

```groovy
compile 'ch.temparus.android:advancedrecyclerview:1.1.0-SNAPSHOT'
```

Maven:
```xml
<dependency>
  <groupId>ch.temparus.android</groupId>
  <artifactId>advancedrecyclerview</artifactId>
  <version>1.1.0-SNAPSHOT</version>
  <type>aar</type>
</dependency>
```

## Usage

You can use the AdvancedRecyclerView the same way as you would use Google's RecyclerView.

To use the advanced functionality of the AdvancedRecyclerView, you should extend your Adapter from the BaseAdapter of this library.

You can find a working example in the ```sample``` directory of this repository.

## Known issue

If your are using header views and the RecyclerView has a height of WRAP_CONTENT, the list starts with the first content item. 
If you call ```adapter.notifyDatasetHasChanged()```, the list behaves as expected.

This error is caused whenever recycler.getViewForPosition(position) is called. See method ```measureChild()```.

If you have an idea how to resolve this issue, please drop me a line or even a pull request. Your help is very appreciated.

## Change Log

See [here](https://github.com/sandrolutz/AdvancedRecyclerView/blob/develop/CHANGELOG.md).

## License

    Copyright 2015 Sandro Lutz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
