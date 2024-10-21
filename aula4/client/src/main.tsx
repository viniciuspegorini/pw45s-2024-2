import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { App } from "./App";
import "./index.css";
import { ChakraProvider } from "@chakra-ui/react";


ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <ChakraProvider>
        
          <Routes>
            <Route path="/*" element={<App />} />
          </Routes>
        
      </ChakraProvider>
    </BrowserRouter>
  </React.StrictMode>
);
