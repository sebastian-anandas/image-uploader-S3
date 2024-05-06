import React, {useEffect, useRef, useState} from "react";
import axios from "axios";
import "./ImageUploader.css";
function ImageUploader() {

    const [selectedFile, setSelectedFile] = useState(null);
    const [files, setFiles] = useState([]);
    const [messageStatus, setMessageStatus] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [uploadedSrc, setUploadedSrc] = useState("");

    const ref = useRef();

    useEffect(() => {
        getFiles();
    }, [])

    const getFiles = () => {

        const url = `http://localhost:8080/S3/image`;
        axios
            .get(url)
            .then(response => {
                setFiles(response.data);
            })
            .catch(error => {
                console.log(error);
            })
    }

    const handleFileChange = event => {
        const file = event.target.files[0];
        console.log(file)

        if(file.type === "image/png" || file.type === "image/jpeg") {
            setSelectedFile(file);
        } else {
            alert("Select Image File Only!");
            setSelectedFile(null);
        }
}

    const formSubmit = event => {
        event.preventDefault();

        if(selectedFile) {
            uploadImageToServer();
        } else {
            alert("Please Select Image!");
        }
    };

    // function to upload Image to Server
    const uploadImageToServer = () => {

        const url = `http://localhost:8080/S3/image`;
        const data = new FormData();
        data.append("file", selectedFile);
        setUploading(true);

        axios
            .post(url, data).then(
            response => {
                console.log(response.data);
                setUploadedSrc(response.data);
                setMessageStatus(true);
                getFiles();
            })
            .catch((error) => {
                console.log(error);
            })
            .finally(() => {
                console.log("Request completed!");
                setUploading(false);
            });
    }

    const handleReset = () => {
        ref.current.value = "";
        setSelectedFile(null);
        setMessageStatus(false);
    }

    return (
        <div className="main flex flex-col items-center justify-center">
            <div className="rounded card w-1/3 border shadow m-4 p-4 bg-gray-100">
                <h1 className="text-2xl">Image Uploader</h1>
                <div className="form-container mt-3">
                    <form action="" onSubmit={formSubmit}>
                        <div className="field-container flex flex-col gap-y-2">
                            <label htmlFor="file">Select Image</label>
                            <input ref={ref} onChange={handleFileChange} type="file" id="file"/>
                        </div>
                        <div className="field-container text-center mt-3">
                            <button type="submit" className="px-3 py-1 bg-blue-700 hover:bg-blue-600 text-white rounded">Upload
                            </button>
                            <button type="button"
                                    onClick={handleReset}
                                className="ml-2 px-3 py-1 bg-orange-700 hover:bg-orange-600 text-white rounded">Clear
                            </button>
                        </div>
                    </form>
                </div>

            {/*  Image upload success message alert  */}
                {messageStatus &&
                    <>
                        <div className="mt-3 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"
                             role="alert">
                            <strong className="font-bold">Success!</strong>
                            <span className="block sm:inline"> Image has been uploaded.</span>
                            <span className="absolute top-0 bottom-0 right-0 px-4 py-3">
                              <svg className="fill-current h-6 w-6 text-green-500" role="button" xmlns="http://www.w3.org/2000/svg"
                                   viewBox="0 0 20 20"><title>Close</title><path
                                  d="M14.348 14.849a1 1 0 0 1-1.414 1.414L10 11.414l-2.934 2.934a1 1 0 1 1-1.414-1.414L8.586 10 5.652 7.066a1 1 0 0 1 1.414-1.414L10 8.586l2.934-2.934a1 1 0 0 1 1.414 1.414L11.414 10l2.934 2.934z"/></svg>
                            </span>
                        </div>
                    </>
                }

                {/*    uploading text and loader    */}
                {uploading && (
                    <div className="p-3 text-center">
                        <div className="flex justify-center items-center my-3">
                            <div className="loader"></div>
                        </div>
                        <h1>Uploading...</h1>
                    </div>
                )}

                {/*   Uploaded Image view   */}
                {
                    messageStatus && <div className="uploaded-view">
                        <img className="h-[300px] mx-auto mt-4 rounded shadow" src={uploadedSrc} alt={files.name}/>
                    </div>
                }


            </div>
        {/*  show Uploaded Image section */}
            <div className="mt-4 px-4 justify-center flex flex-wrap">
                {files.map(img => (
                    <img className="h-[200px] m-2 shadow rounded" src={img} key={img} alt={files.name}/>
                ))}
            </div>

        </div>
    );
}

export default ImageUploader;