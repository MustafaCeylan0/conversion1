import sys
from PIL import Image

def convert_image(input_path, output_path):
    # Extracting the output format from the output path
    output_format = output_path.rsplit('.', 1)[-1]

    with Image.open(input_path) as img:
        # If the output format is JPEG and the image has an alpha channel, remove it
        if output_format.upper() == 'JPEG' and img.mode == 'RGBA':
            img = img.convert('RGB')

        img.save(output_path, output_format.upper())
        print(f"Image saved as {output_path}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py <input_path> <output_path>")
    else:
        input_path = sys.argv[1]
        output_path = sys.argv[2]

        # Checking if the output format is supported
        supported_formats = ['jpeg', 'png', 'bmp', 'gif', 'tiff']
        output_format = output_path.rsplit('.', 1)[-1].lower()
        if output_format not in supported_formats:
            print("Unsupported file format!")
        else:
            convert_image(input_path, output_path)
