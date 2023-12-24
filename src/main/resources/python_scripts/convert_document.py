import subprocess
import sys
import os

def convert_document(input_path, output_path):
    if not os.path.isfile(input_path):
        print(f"Error: Input file '{input_path}' does not exist.")
        return

    output_format = output_path.rsplit('.', 1)[-1].lower()
    unoconv_formats = ['pdf', 'doc', 'docx','rtf', 'xml', 'odt', 'ott']
    pandoc_formats = ['md', 'html', 'epub', ]
    print("output_format: " + output_format)
    try:
        if output_format in pandoc_formats:
            subprocess.check_call(['pandoc', '-o', output_path, input_path])
            print(f"Document converted to {output_format} using pandoc and saved as {output_path}")
        elif output_format in unoconv_formats:
            subprocess.check_call(['unoconv', '-f', output_format, '-o', output_path, input_path])
            print(f"Document converted to {output_format} using unoconv and saved as {output_path}")
        else:
            print("Unsupported file format for conversion!")
    except subprocess.CalledProcessError as e:
        print(f"An error occurred during conversion: {e}")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python convert_document.py <input_path> <output_path>")
    else:
        input_path = sys.argv[1]
        output_path = sys.argv[2]
        convert_document(input_path, output_path)
