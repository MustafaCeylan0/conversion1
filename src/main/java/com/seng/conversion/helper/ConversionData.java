package com.seng.conversion.helper;

import java.util.Arrays;
import java.util.List;

public class ConversionData {
    public static final List<ConversionHelper> conversionHelpers = Arrays.asList(
            // PDF conversions

            new ConversionHelper("pdf", "docx", "pdf_to_x.py"),
            new ConversionHelper("pdf", "txt", "pdf_to_x.py"),
            new ConversionHelper("pdf", "jpg", "pdf_to_x.py"),
            new ConversionHelper("pdf", "png", "pdf_to_x.py"),
            // DOC conversion,
            new ConversionHelper("doc", "pdf", "doc_to_pdf.py"),
            new ConversionHelper("doc", "docx", "doc_to_docx.py"),
            new ConversionHelper("doc", "html", "doc_to_html.py"),
            new ConversionHelper("doc", "txt", "doc_to_txt.py"),
            new ConversionHelper("doc", "jpg", "doc_to_jpg.py"),
            new ConversionHelper("doc", "png", "doc_to_png.py"),
            // DOCX conversion,
            new ConversionHelper("docx", "pdf", "convert_document.py"),
            new ConversionHelper("docx", "doc", "docx_to_doc.py"),
            new ConversionHelper("docx", "html", "docx_to_html.py"),
            new ConversionHelper("docx", "txt", "docx_to_txt.py"),
            new ConversionHelper("docx", "jpg", "docx_to_jpg.py"),
            new ConversionHelper("docx", "png", "docx_to_png.py"),
            // HTML conversion,
            new ConversionHelper("html", "pdf", "html_to_pdf.py"),
            new ConversionHelper("html", "docx", "html_to_docx.py"),
            new ConversionHelper("html", "doc", "html_to_doc.py"),
            new ConversionHelper("html", "txt", "html_to_txt.py"),
            new ConversionHelper("html", "jpg", "html_to_jpg.py"),
            new ConversionHelper("html", "png", "html_to_png.py"),
            // TXT conversion,
            new ConversionHelper("txt", "pdf", "txt_to_pdf.py"),
            new ConversionHelper("txt", "docx", "txt_to_docx.py"),
            new ConversionHelper("txt", "doc", "txt_to_doc.py"),
            new ConversionHelper("txt", "html", "txt_to_html.py"),
            // JPG conversion,
            new ConversionHelper("jpg", "pdf", "jpg_to_pdf.py"),
            new ConversionHelper("jpg", "png", "jpg_to_png.py"),
            new ConversionHelper("jpg", "tiff", "jpg_to_tiff.py"),
            new ConversionHelper("jpg", "bmp", "jpg_to_bmp.py"),
            new ConversionHelper("jpg", "gif", "jpg_to_gif.py"),
            // PNG conversion,
            new ConversionHelper("png", "pdf", "png_to_pdf.py"),
            new ConversionHelper("png", "jpg", "png_to_jpg.py"),
            new ConversionHelper("png", "tiff", "png_to_tiff.py"),
            new ConversionHelper("png", "bmp", "png_to_bmp.py"),
            new ConversionHelper("png", "gif", "png_to_gif.py"),
            // XLSX conversion,
            new ConversionHelper("xlsx", "pdf", "xlsx_to_pdf.py"),
            new ConversionHelper("xlsx", "xls", "xlsx_to_xls.py"),
            new ConversionHelper("xlsx", "csv", "xlsx_to_csv.py"),
            // PPTX conversion,
            new ConversionHelper("pptx", "pdf", "pptx_to_pdf.py"),
            new ConversionHelper("pptx", "ppt", "pptx_to_ppt.py"),
            new ConversionHelper("pptx", "jpg", "pptx_to_jpg.py"),
            new ConversionHelper("pptx", "png", "pptx_to_png.py")
    );

    public static String inputPath = "src/main/resources/database/input";
    public static String outputPath = "src/main/resources/database/output";
    public static String pythonScriptsPath = "src/main/resources/python_scripts";
}
