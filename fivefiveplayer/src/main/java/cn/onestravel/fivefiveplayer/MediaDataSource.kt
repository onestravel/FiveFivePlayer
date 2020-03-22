package cn.onestravel.fivefiveplayer

import android.net.Uri


/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description TODO
 */
class MediaDataSource {
    var title: String = ""
    var uri: Uri? = null
    var isLooping: Boolean = false

    constructor() {}
    constructor(uri: Uri?) {
        this.uri = uri
    }

    constructor(title: String, uri: Uri?) {
        this.title = title
        this.uri = uri
    }

    constructor(title: String, uri: Uri?, loop: Boolean) {
        this.title = title
        this.uri = uri
        this.isLooping = loop
    }

}