package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

//import java.util.List;

// ***********************************

    public class PDFActivity extends Activity implements OnPageChangeListener,OnLoadCompleteListener{
        private static final String TAG = com.scorer.tennis_android_nov22.MainActivity.class.getSimpleName();
        public static final String SAMPLE_FILE = "2022-rules-of-tennis-english.pdf";
        PDFView pdfView;
        Integer pageNumber = 0;
        String pdfFileName;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pdfviewer);

            pdfView= (PDFView)findViewById(R.id.pdfView);
            displayFromAsset();
        }

// ***********************************

        private void displayFromAsset() {
            pdfFileName = PDFActivity.SAMPLE_FILE;

            pdfView.fromAsset(SAMPLE_FILE)
                    .defaultPage(pageNumber)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .enableDoubletap(false)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .load();
        }

        // ***********************************

        @Override
        public void onPageChanged(int page, int pageCount) {
            pageNumber = page;
            setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
        }

        // ***********************************

        @Override
        public void loadComplete(int nbPages) {
            PdfDocument.Meta meta = pdfView.getDocumentMeta();
//            printBookmarksTree(pdfView.getTableOfContents(), "-");
        }

//        public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
//            for (PdfDocument.Bookmark b : tree) {
//
//                Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
//
//                if (b.hasChildren()) {
//                    printBookmarksTree(b.getChildren(), sep + "-");
//                }
//            }
//        }

// ***********************************

        public void onClick_Exit(View view) {
            finish();
        }
    }








