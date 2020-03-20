package cn.onestravel.fivefiveplayer

import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import cn.onestravel.fivefiveplayer.interf.PlayerCallBack
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.kernel.MediaKernelInterface
import cn.onestravel.fivefiveplayer.kernel.MediaPlayerKernel
import cn.onestravel.fivefiveplayer.utils.LogHelper
import cn.onestravel.fivefiveplayer.view.VideoTextureView

/**
 * Created by wanghu on 2020/3/19
 */
class FivePlayer {
    private var mUri: Uri? = null
    private var mMediaKernel: MediaKernelInterface = MediaPlayerKernel(this)
    private var mState: Int = PlayerInterface.STATE_IDLE
    private var mPlayerCallBack: PlayerCallBack? = null
    private var mTextureView: VideoTextureView? = null

    fun attachTextureView(textureView: VideoTextureView) {
        this.mTextureView = textureView
        mTextureView?.let {
            it.surfaceTextureListener = mMediaKernel
            it.isOpaque = false
            it.isActivated = true
        }

    }


    /**
     * 设置回调监听
     */
    fun setPlayerCallBack(playerCallBack: PlayerCallBack) {
        this.mPlayerCallBack = playerCallBack
    }

    /**
     * 获取当前状态
     */
    fun getState(): Int {
        return mState;
    }

    /**
     * 设置播放器媒体内核
     */
    fun setMediaKernel(mediaKernel: MediaKernelInterface) {
        this.mMediaKernel = mMediaKernel;
    }

    /**
     * 设置媒体源
     */
    fun setDataSource(url: String) {
        mUri = Uri.parse(url)
        mUri?.let {
            val mediaDataSource =
                MediaDataSource(it)
            mMediaKernel.prepare(mediaDataSource)
            mState = PlayerInterface.STATE_PREPARING
        }
    }

    /**
     * 开始播放
     */
    fun start() {
        mMediaKernel.start()
    }

    /**
     * 从指定位置开始播放
     */
    fun start(position: Long) {
        mMediaKernel.start(position)
    }

    /**
     * 暂停播放
     */
    fun pause() {
        mMediaKernel.pause()
        onPaused()
    }

    /**
     * 停止播放
     */
    fun stop() {
        mMediaKernel.stop()
        onStopped()
    }

    /**
     * 继续播放
     */
    fun resume() {
        mMediaKernel.resume()
        onResume()
        onPlaying()
    }

    private fun onResume() {
        mPlayerCallBack?.let {
            it.onResume()
        }
    }

    /**
     * 定位视频
     */
    fun seekTo(position: Long) {
        mMediaKernel.seekTo(position)
    }


    /**
     * 获取视频总时长
     */
    fun getDuration(): Long {
        return mMediaKernel.getDuration()
    }

    /**
     * 获取视频当前位置
     */
    fun getCurrentPosition(): Long {
        return mMediaKernel.getCurrentPosition()
    }

    /**
     * 获取视频是否正在播放
     */
    fun isPlaying(): Boolean {
        return mMediaKernel.isPlaying()
    }

    /**
     * 设置播放器音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mMediaKernel.setVolume(leftVolume, rightVolume)
    }

    /**
     * 设置播放器倍速
     */
    fun setSpeed(speed: Float) {
        mMediaKernel.setSpeed(speed)
    }

    /**
     * 设置播放器SurfaceTexture
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setSurfaceTexture(surface: SurfaceTexture) {
        mTextureView?.let {
            it.surfaceTexture = surface
        }
        mPlayerCallBack?.let { it.onSetSurfaceTexture(surface) }
    }

    /**
     * 播放器重置
     */
    fun reset() {
        mMediaKernel.reset()
        mProgressHandler.removeCallbacks(mProgressTicker)
    }

    /**
     * 播放器释放
     */
    fun release() {
        mMediaKernel.release()
        mProgressHandler.removeCallbacks(mProgressTicker)
    }

    /**
     * 播放器准备完成回调
     */
    fun onPrepared() {
        mState = PlayerInterface.STATE_PREPARED
        mPlayerCallBack?.let {
            it.onPrepared()
        }
    }

    /**
     * 开始渲染图像
     */
    fun onStartRender() {
        if (mState == PlayerInterface.STATE_PREPARED) {
            mState = PlayerInterface.STATE_PLAYING
        }
        onPlaying()
        mPlayerCallBack?.let {
            it.onStart()
        }
    }

    /**
     * 停止播放回调
     */
    fun onStopped() {
        mState = PlayerInterface.STATE_STOP
        mProgressHandler.removeCallbacks(mProgressTicker)
        mPlayerCallBack?.let {
            it.onStopped()
        }
    }

    /**
     * 暂停播放回调
     */
    fun onPaused() {
        mState = PlayerInterface.STATE_PAUSED
        mProgressHandler.removeCallbacks(mProgressTicker)
        mPlayerCallBack?.let {
            it.onPaused()
        }
    }

    /**
     * 缓冲播放
     */
    fun onBufferingPlaying() {
        mState = PlayerInterface.STATE_BUFFERING_PLAYING
    }

    /**
     * 暂时不播放，以缓冲更多的数据
     */
    fun onBufferingPaused() {
        mState = PlayerInterface.STATE_BUFFERING_PAUSED
        mProgressHandler.removeCallbacks(mProgressTicker)
    }

    /**
     * 正在播放
     */
    fun onPlaying() {
        mState = PlayerInterface.STATE_PLAYING
        mProgressHandler.post(mProgressTicker)
    }

    /**
     * 视频跳转到指定位置成功回调
     */
    fun onSeekTo(position: Long) {
        mPlayerCallBack?.let {
            it.onSeekTo(position)
        }
    }

    /**
     * 视频跳转到指定位置成功回调
     */
    fun onSeekComplete() {
        onSeekTo(getCurrentPosition())
    }

    /**
     * 缓冲更新回调
     */
    fun onBufferingUpdate(percent: Int) {

    }

    /**
     * 视频播放进度更新回调
     */
    fun onProgressChanged(total: Long, progress: Long) {
        mPlayerCallBack?.let {
            it.onProgressChanged(total, progress)
        }
    }

    /**
     * 视频播放完成回调
     */
    fun onCompletion() {
        mState = PlayerInterface.STATE_COMPLETE
        mPlayerCallBack?.let {
            it.onCompletion()
        }
        mProgressHandler.removeCallbacks(mProgressTicker)
    }

    /**
     * 视频播放异常回调
     */
    fun onError(e: Exception) {
        mState = PlayerInterface.STATE_ERROR
        LogHelper.e(mMediaKernel.javaClass.simpleName, "Player Exception:", e)
        mPlayerCallBack?.let {
            it.onError(e)
        }
    }

    /**
     * 视频大小改变回调
     */
    fun onVideoSizeChanged(width: Int, height: Int) {
        mTextureView?.let {
            it.resetVideoSize(width, height)
        }
        mPlayerCallBack?.let {
            it.onVideoSizeChanged(width, height)
        }
    }


    //为多线程定义Handler
    private val mProgressHandler = Handler()

    /**
     * 定义一个Runnable对象
     * 用于更新播发进度
     */
    private val mProgressTicker: Runnable = object : Runnable {
        override fun run() {
            //延迟200ms再次执行runnable,就跟计时器一样效果
            mProgressHandler.postDelayed(this, 200)
            //更新播放进度
            onProgressChanged(getDuration(), getCurrentPosition())
        }
    }

}