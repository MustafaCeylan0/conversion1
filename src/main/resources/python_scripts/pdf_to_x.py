import sys
import fitz  # PyMuPDF
from pdf2docx import Converter
from pdfminer.high_level import extract_text
import tabula

def pdf_to_docx(input_file, output_file):
    try:
        cv = Converter(input_file)
        cv.convert(output_file, start=0, end=None)
        cv.close()
    except Exception as e:
        print(f"Error converting PDF to DOCX: {e}", file=sys.stderr)
        sys.exit(1)

def pdf_to_txt(input_file, output_file):
    try:
        text = extract_text(input_file)
        with open(output_file, "w") as f:
            f.write(text)
    except Exception as e:
        print(f"Error converting PDF to TXT: {e}", file=sys.stderr)
        sys.exit(1)

def pdf_to_image(input_file, output_file, img_format):
    try:
        doc = fitz.open(input_file)
        for page_num, page in enumerate(doc):
            pix = page.get_pixmap()
            output_filename = output_file.replace(".png", f"_{page_num}.{img_format}") if img_format == "png" else output_file.replace(".jpg", f"_{page_num}.{img_format}")
            pix.save(output_filename)
    except Exception as e:
        print(f"Error converting PDF to Image: {e}", file=sys.stderr)
        sys.exit(1)

def pdf_to_xlsx(input_file, output_file):
    try:
        tabula.convert_into(input_file, output_file, output_format="xlsx", pages='all')
    except Exception as e:
        print(f"Error converting PDF to XLSX: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python convert_document.py [input_file] [output_file]", file=sys.stderr)
        sys.exit(1)

    input_path = sys.argv[1]
    output_path = sys.argv[2]
    output_extension = output_path.split(".")[-1]

    try:
        if output_extension == "docx":
            pdf_to_docx(input_path, output_path)
        elif output_extension == "txt":
            pdf_to_txt(input_path, output_path)
        elif output_extension in ["jpg", "png"]:
            pdf_to_image(input_path, output_path, output_extension)
        elif output_extension == "xlsx":
            pdf_to_xlsx(input_path, output_path)
        else:
            print(f"Unsupported output format: {output_extension}", file=sys.stderr)
            sys.exit(1)
    except Exception as e:
        print(f"General error: {e}", file=sys.stderr)
        sys.exit(1)
