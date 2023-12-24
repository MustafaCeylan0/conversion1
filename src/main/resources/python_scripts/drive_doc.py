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
            flow = InstalledAppFlow.from_client_secrets_file('src/main/resources/PythonScripts/credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)
    return build('drive', 'v3', credentials=creds)

def get_mimetype_for_conversion(file_extension):
    conversion_map = {
        '.doc': 'application/vnd.google-apps.document',
        '.docx': 'application/vnd.google-apps.document',
        '.html': 'application/vnd.google-apps.document',
        '.txt': 'application/vnd.google-apps.document',
        '.rtf': 'application/vnd.google-apps.document',
        '.pdf': 'application/vnd.google-apps.document',
        '.xls': 'application/vnd.google-apps.spreadsheet',
        '.xlsx': 'application/vnd.google-apps.spreadsheet',
        '.csv': 'application/vnd.google-apps.spreadsheet',
        '.tsv': 'application/vnd.google-apps.spreadsheet',
        '.ods': 'application/vnd.google-apps.spreadsheet',
        '.ppt': 'application/vnd.google-apps.presentation',
        '.pptx': 'application/vnd.google-apps.presentation'
    }
    return conversion_map.get(file_extension.lower())

def upload_and_convert_file(service, file_path, target_mimetype):
    file_metadata = {
        'name': os.path.basename(file_path),
        'mimeType': target_mimetype
    }
    media = MediaFileUpload(file_path, mimetype='application/octet-stream', resumable=True)
    file = service.files().create(body=file_metadata, media_body=media, fields='id').execute()
    print(f"Uploaded File ID: {file.get('id')}")
    return file.get('id')

def download_file(service, file_id, output_file_path, output_mimetype):
    request = service.files().export_media(fileId=file_id, mimeType=output_mimetype)
    fh = io.BytesIO()
    downloader = MediaIoBaseDownload(fh, request)
    done = False
    while not done:
        status, done = downloader.next_chunk()
    with open(output_file_path, 'wb') as f:
        f.write(fh.getbuffer())
    print(f"File downloaded to {output_file_path}")

def main(input_file_path, output_file_path):
    service = service_account_login()

    # Determine MIME types
    input_extension = os.path.splitext(input_file_path)[1]
    output_extension = os.path.splitext(output_file_path)[1]

    input_mimetype = get_mimetype_for_conversion(input_extension)
    if not input_mimetype:
        print(f"Conversion from {input_extension} is not supported.")
        return

    output_mimetype = 'application/pdf'  # Default to PDF for the output
    if output_extension in ['.docx', '.xlsx', '.pptx']:
        output_mimetype = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'

    # Upload and convert file
    file_id = upload_and_convert_file(service, input_file_path, input_mimetype)
    if file_id:
        download_file(service, file_id, output_file_path, output_mimetype)
    else:
        print("Failed to upload and convert file.")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python script.py input_file_path output_file_path")
    else:
        input_file_path = sys.argv[1]
        output_file_path = sys.argv[2]
        main(input_file_path, output_file_path)
