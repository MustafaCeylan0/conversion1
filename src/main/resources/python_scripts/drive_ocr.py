import os
import io
import sys
import pickle
from googleapiclient.http import MediaIoBaseDownload, MediaFileUpload
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request

# Scopes define the level of access the application has.
SCOPES = ['https://www.googleapis.com/auth/drive']

def service_account_login():
    creds = None
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file('credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)
    return build('drive', 'v3', credentials=creds)

def get_image_mimetype(file_path):
    extension_to_mimetype = {
        '.jpg': 'image/jpeg',
        '.jpeg': 'image/jpeg',
        '.png': 'image/png',
        '.gif': 'image/gif',
        '.bmp': 'image/bmp',
        '.tiff': 'image/tiff',
        '.svg': 'image/svg+xml'
    }
    extension = os.path.splitext(file_path)[1].lower()
    return extension_to_mimetype.get(extension, 'application/octet-stream')

def upload_and_ocr_image(service, image_path):
    image_mimetype = get_image_mimetype(image_path)
    file_metadata = {
        'name': os.path.basename(image_path),
        'mimeType': 'application/vnd.google-apps.document'
    }
    media = MediaFileUpload(image_path, mimetype=image_mimetype, resumable=True)
    file = service.files().create(body=file_metadata, media_body=media, fields='id').execute()
    print(f"Image uploaded and OCR started, File ID: {file.get('id')}")
    return file.get('id')

def download_extracted_text(service, file_id, output_file_path):
    request = service.files().export_media(fileId=file_id, mimeType='text/plain')
    fh = io.BytesIO()
    downloader = MediaIoBaseDownload(fh, request)
    done = False
    while not done:
        status, done = downloader.next_chunk()
    with open(output_file_path, 'wb') as f:
        f.write(fh.getbuffer())
    print(f"Extracted text downloaded to {output_file_path}")

def main(image_path, output_file_path):
    service = service_account_login()
    file_id = upload_and_ocr_image(service, image_path)
    if file_id:
        download_extracted_text(service, file_id, output_file_path)
    else:
        print("Failed to upload image and perform OCR.")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python script.py image_path output_file_path")
    else:
        image_path = sys.argv[1]
        output_file_path = sys.argv[2]
        main(image_path, output_file_path)
