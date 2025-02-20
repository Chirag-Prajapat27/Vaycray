package com.halfeaten.vaycray.host.photoUpload

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.PickerType
import com.halfeaten.vaycray.*
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentUploadListphotoBinding
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.profile.edit_profile.RC_LOCATION_PERM
import com.halfeaten.vaycray.util.*
import com.halfeaten.vaycray.vo.PhotoList
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import net.gotev.uploadservice.*
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class UploadPhotoActivity : BaseActivity<HostFragmentUploadListphotoBinding, Step2ViewModel>(),
    EasyPermissions.PermissionCallbacks,
    Step2Navigator, ImagePickerResultListener {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: HostFragmentUploadListphotoBinding
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_upload_listphoto
    override val viewModel: Step2ViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(Step2ViewModel::class.java)
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var isReCreated = false
    private val imagePicker: ImagePicker by lazy {
        registerImagePicker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this

        if (viewModel.uiMode == null)
            viewModel.uiMode = resources.configuration.uiMode
        else
            isReCreated = true

        mBinding.pgBar.progress = 50
        mBinding.rvListPhotos.gone()
        mBinding.tvNext.gone()
        initView()
        subscribeToLiveData()
        initPicker()
        if (isReCreated) {
            viewModel.photoList.observe(this) {
                setup(it!!);
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }})

    }

    private fun initView() {
        viewModel.setInitialValuesFromIntent(intent)

        mBinding.uploadToolbar.ivNavigateup.onClick { navigateBack() }
        mBinding.tvNext.onClick {
            replaceFragment(AddListTitleFragment(), "AddListPhoto")
        }
        mBinding.rvListPhotos.layoutManager = GridLayoutManager(this, 2)
        viewModel.getListDetailsStep2()
    }

    private fun setup(it: ArrayList<PhotoList>) {
        try {
            mBinding.chips.apply {
                paddingBottom = true
                photos = true
                title = false
                description = false
                photosClick = (View.OnClickListener {

                })
                titleClick = (View.OnClickListener {
                    viewModel.navigator.navigateToScreen(Step2ViewModel.NextScreen.LISTTITLE)
                })
            }
            mBinding.rvListPhotos.withModels {

                viewholderAddListingPhotos {
                    id("add_photos")
                    onClick(View.OnClickListener {
                        Utils.clickWithDebounce(it) {
                            askCameraPermission()
                        }
                    })
                }
                it.forEachIndexed { index, s ->
                    viewholderAddListing {
                        id(getString(R.string.photo) + s.refId)
                        url(s.name)
                        isRetry(s.isRetry)
                        isLoading(s.isLoading)
                        onClick(View.OnClickListener {

                        })
                        onRetryClick { _ ->
                            viewModel.retryPhotos(s.refId)
                            uploadMultipartSeperate(s.name!!, s.refId)
                        }
                        deleteClick(View.OnClickListener {
                            viewModel.retryCalled = "delete-${s.name}"
                            viewModel.deletephoto(s.name!!)
                        })
                        if (viewModel.getCoverPhotoId() == s.id) {
                            onSelected(true)
                        } else {
                            onSelected(false)
                        }
                        if (it.size == 1) {
                            onSelected(true)
                        }
                        if (viewModel.coverPhoto.value == -2) {
                            if (index == 0) {
                                onSelected(true)
                            }
                        }
                        onClickq(View.OnClickListener {
                            viewModel.coverPhoto.value = s.id
                            this@withModels.requestModelBuild()
                        })
                    }
                }
            }
        } catch (E: java.lang.Exception) {
            showError()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }


    fun uploadMultipartSeperate(uri: String, id: String) {
        try {

            val a = MultipartUploadRequest(this, Constants.WEBSITE + Constants.uploadListPhoto)
            a.setMethod("POST")
            //  a.setBasicAuth(Constants.USERNAME,Constants.PASSWORD)
            a.addFileToUpload(Uri.parse(uri).path!!, "file")
            a.addHeader("auth", viewModel.getAccessToken())
            a.addParameter("listId", viewModel.listID.value.toString())
            //a.setNotificationConfig { context, uploadId -> config }
            a.setMaxRetries(2)
            a.subscribe(context = baseContext, lifecycleOwner = this, delegate = object :
                RequestObserverDelegate {
                override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                }

                override fun onSuccess(
                    context: Context,
                    uploadInfo: UploadInfo,
                    serverResponse: ServerResponse
                ) {
                    Timber.tag("errorMessage").d(
                        "upload - onComplete - %d, response - %s, id - %s",
                        uploadInfo?.progressPercent,
                        serverResponse?.bodyString,
                        uploadInfo?.uploadId
                    )
                    viewModel.setCompleted(serverResponse.bodyString!!)
                }

                override fun onError(
                    context: Context,
                    uploadInfo: UploadInfo,
                    exception: Throwable
                ) {
                    viewModel.setError(uploadInfo.uploadId)
                    when (exception) {
                        is UserCancelledUploadException -> {
                        }

                        is UploadError -> {
                        }

                        else -> {
                        }
                    }
                }

                override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
                    // do your thing
                }

                override fun onCompletedWhileNotObserving() {
                    // do your thing
                }
            })

        } catch (exc: Exception) {
            // Log.e("FragmentActivity.TAG", exc.message, exc)
        }
    }

    fun subscribeToLiveData() {
        mBinding.tvNext.text = getString(R.string.skip_for_now)
        viewModel.photoList.observe(this, Observer {
            it?.let {
                if (!viewModel.title.get().isNullOrEmpty() && !viewModel.desc.get()
                        .isNullOrEmpty() && !it.isNullOrEmpty()
                ) {
                    mBinding.tvRightsideText.text = getText(R.string.save_and_exit)
                    mBinding.tvRightsideText.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )
                    mBinding.tvRightsideText.visibility = View.VISIBLE
                    mBinding.tvRightsideText.setOnClickListener {
                        if (viewModel.checkFilledData()) {
                            it.disable()
                            viewModel.retryCalled = "update"
                            viewModel.updateStep2()
                        }
                    }

                } else {

                    mBinding.tvRightsideText.visibility = View.GONE
                    mBinding.chips.chips2.gone()


                }
                mBinding.visiblity = true
                if (it.size > 0) {
                    viewModel.photoListSize = 1
                    mBinding.tvNext.text = getString(R.string.next)
                    mBinding.tvNext.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white_photo_story
                        )
                    )
                    mBinding.tvNext.setBackgroundResource(R.drawable.curve_button_red)
                } else {
                    viewModel.coverPhoto.value = -2
                    viewModel.photoListSize = 0
                    mBinding.tvNext.setText(getString(R.string.skip_for_now))
                }
                if (mBinding.rvListPhotos.adapter == null) {
                    if (viewModel.isListActive) {
                        mBinding.rvListPhotos.visible()
                        mBinding.tvNext.visible()
                    } else {
                        mBinding.rvListPhotos.gone()
                        mBinding.tvNext.gone()
                    }
                    setup(it)
                } else {
                    mBinding.rvListPhotos.requestModelBuild()
                }
                mBinding.rvListPhotos.requestModelBuild()

            }
        })
        viewModel.step2Result.observe(this, Observer {
            it?.let {
                if (!viewModel.title.get().isNullOrEmpty() && !viewModel.desc.get()
                        .isNullOrEmpty() && !viewModel.photoList.value.isNullOrEmpty()
                ) {
                    mBinding.tvRightsideText.text = getText(R.string.save_and_exit)
                    mBinding.tvRightsideText.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )
                    mBinding.chips.chips2.visible()
                    mBinding.tvRightsideText.setOnClickListener {
                        if (viewModel.checkFilledData()) {
                            it.disable()
                            viewModel.retryCalled = "update"
                            viewModel.updateStep2()
                        }
                    }

                }
            }
        })
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun askCameraPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            if (EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            ) {
                pickImage()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Grant Permission to access your gallery and photos",
                    com.halfeaten.vaycray.ui.profile.edit_profile.RC_LOCATION_PERM,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            }
        } else {
            if (EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            ) {
                pickImage()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Grant Permission to access your gallery and photos",
                    com.halfeaten.vaycray.ui.profile.edit_profile.RC_LOCATION_PERM,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.tag("ProfileFragment").d("Camera Permission Denied!!")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Timber.tag("ProfileFragment").d("Camera Permission Denied!!")
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}


    private fun pickImage() {
        imagePicker
            .title(resources.getString(R.string.app_name))
            .multipleSelection(enable = true, maxCount = 10)
            .showCountInToolBar(true)
            .showFolder(true)
            .cameraIcon(false)
            .doneIcon(true)
            .allowCropping(true)
            .compressImage(false)
        imagePicker.open(PickerType.GALLERY)
    }

    private fun pickImage13() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }

    private fun initPicker() {
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uris != null) {
                    val photoPaths = ArrayList<Uri>()
                    photoPaths.addAll(uris!!)
                    validateFileSize(photoPaths)
                }
            }
    }


    private fun validateFileSize(photoPaths: List<Uri>?) {
        var noOfFilesLargeInSize = 0
        val toUploadPhotos = ArrayList<PhotoList>()

        photoPaths?.forEach {
            val file = File(getPath(it))
            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB <= Constants.LISTINGIMAGESIZEINMB) {
                val list = viewModel.addPhoto(getPath(it)!!)
                toUploadPhotos.add(list)
                uploadMultipartSeperate(list.name!!, list.refId)
            } else {
                noOfFilesLargeInSize++
            }
        }
        if (noOfFilesLargeInSize == 1) {
            showToast(getString(R.string.image_size_is_too_large))
        } else if (noOfFilesLargeInSize > 1) {
            showToast("$noOfFilesLargeInSize " + getString(R.string.image_size_is_too_large))
        }
        viewModel.addPhotos(toUploadPhotos)
    }

    fun getPath(uri: Uri): String? {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (isKitKat) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                val fullPath = getPathFromExtSD(split)
                return if (fullPath !== "") {
                    fullPath
                } else {
                    null
                }
            }


            if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val id: String
                    var cursor: Cursor? = null
                    try {
                        cursor = this.contentResolver.query(
                            uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val fileName: String = cursor.getString(0)
                            val path: String = Environment.getExternalStorageDirectory().toString()
                                .toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return path
                            }
                        }
                    } finally {
                        if (cursor != null) cursor.close()
                    }
                    id = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            return try {
                                val contentUri = ContentUris.withAppendedId(
                                    Uri.parse(contentUriPrefix),
                                    java.lang.Long.valueOf(id)
                                )
                                getDataColumn(this, contentUri, null, null)
                            } catch (e: NumberFormatException) {
                                uri.path?.replaceFirst("^/document/raw:".toRegex(), "")
                                    ?.replaceFirst("^raw:".toRegex(), "")
                            }
                        }
                    }
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )

                        if (contentUri != null) {
                            return getDataColumn(this, contentUri, null, null)
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }

                }
            }


            if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    this, contentUri, selection,
                    selectionArgs
                )
            }
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri)
            }
            if (isWhatsAppFile(uri)) {
                return getFilePathForWhatsApp(uri)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri)
                }
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    copyFileToInternalStorage(uri, "userfiles")
                } else {
                    getDataColumn(this, uri, null, null)
                }
            }
            if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else {
            if (isWhatsAppFile(uri)) {
                return getFilePathForWhatsApp(uri)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index: Int? =
                        cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor?.moveToFirst() == true) {
                        return cursor?.getString(column_index!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    private fun getPathFromExtSD(pathData: Array<String>): String {
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath = ""

        if ("primary".equals(type, ignoreCase = true)) {
            fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }


        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    private fun getDriveFilePath(uri: Uri): String? {
        val returnCursor: Cursor? =
            this.contentResolver!!.query(uri, null, null, null, null)
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(this.cacheDir, name)
        try {
            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also({ read = it }) != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message.orEmpty())
        }
        return file.path
    }

    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor? = this.contentResolver.query(
            uri, arrayOf(
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
            ), null, null, null
        )


        /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val output: File
        output = if (newDirName != "") {
            val dir = File(this.filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            File(this.filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            File(this.filesDir.toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers).also({ read = it }) != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("Exception", e.message.orEmpty())
        }
        return output.path
    }

    private fun getFilePathForWhatsApp(uri: Uri): String? {
        return copyFileToInternalStorage(uri, "whatsapp")
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isWhatsAppFile(uri: Uri): Boolean {
        return "com.whatsapp.provider.media" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        replaceFragmentInActivity(mBinding.flStepTwo.id, fragment, tag)
    }

    fun popFragment(fragment: Fragment, tag: String) {
        replaceFragmentToActivity(mBinding.flStepTwo.id, fragment, tag)
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }


    fun navigateBack(){
        hideKeyboard()
        val myFrag = supportFragmentManager.findFragmentByTag("Photos")
        val myFrag2 = supportFragmentManager.findFragmentByTag("AddListPhoto")
        if (myFrag != null && myFrag.isVisible) {
            this.finish()
        } else if (myFrag2!= null && myFrag2.isVisible){
            supportFragmentManager.findFragmentByTag("AddListPhoto")?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
        } else {
            this.finish()
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_right, R.anim.slide_left)
            .replace(mBinding.flStepTwo.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun navigateToScreen(screen: Step2ViewModel.NextScreen, vararg params: String?) {
        when (screen) {
            Step2ViewModel.NextScreen.UPLOAD -> {
                supportFragmentManager.findFragmentByTag("AddListPhoto")
                    ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            }

            Step2ViewModel.NextScreen.LISTTITLE -> {
                replaceFragment(AddListTitleFragment(), "AddListPhoto")
            }

            Step2ViewModel.NextScreen.FINISH -> {
                this.finish()
            }

            else -> {}
        }
    }

    override fun navigateBack(backScreen: Step2ViewModel.BackScreen) {
        hideKeyboard()
        when (backScreen) {
            Step2ViewModel.BackScreen.UPLOAD -> {
                supportFragmentManager.findFragmentByTag("AddListPhoto")
                    ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
                supportFragmentManager.findFragmentByTag("ListDesc")
                    ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            }

            Step2ViewModel.BackScreen.LISTTITLE -> {
                popFragment(AddListTitleFragment(), "AddListPhoto")
            }

            Step2ViewModel.BackScreen.FINISH -> {}
            else -> {}
        }
    }

    override fun onRetry() {
        if (viewModel.retryCalled.equals("")) {
            viewModel.getListDetailsStep2()
        } else if (viewModel.retryCalled.contains("delete")) {
            val text = viewModel.retryCalled.split("-")
            viewModel.deletephoto(text[1])
        } else {
            viewModel.updateStep2()
        }
    }

    override fun show404Page() {
        showToast(getString(R.string.list_not_available))
        this.finish()
    }

    override fun onImagePick(uri: Uri?) {
        if (uri!=null)
            validateFileSize(listOf(uri))
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        validateFileSize(uris)
    }
}