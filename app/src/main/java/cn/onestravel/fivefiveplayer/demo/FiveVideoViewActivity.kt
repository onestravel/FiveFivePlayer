package cn.onestravel.fivefiveplayer.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_five_video_view.*

/**
 * Created by onestravel on 2020/3/20
 */
open class FiveVideoViewActivity : AppCompatActivity() {
    //    val path: String = (getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
//        ?: "") + "/Video/OctoMaker_4760_Sand Balls.mp4";
    val path: String = "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4"
//    val path: String = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    private var rotation: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_five_video_view)
        ivRotation.setOnClickListener {
            rotation += 90
            if (rotation > 270) {
                rotation = 0f
            }
            fiveVideoView.setVideoRotation(rotation)
        }
        initData()
    }

    private fun initData() {
        fiveVideoView.setDataSource(path)
        fiveVideoView.setOnPreparedListener {
            it.start()
        }
        fiveVideoView.setOnClickListener { }
    }
}