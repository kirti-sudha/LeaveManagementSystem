import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Injectable({
  providedIn: 'root'
})
export class PdfExportService {

  constructor() { }

  /**
   * Exports an array of data to a PDF file.
   * @param headers An array of strings for the table headers.
   * @param data An array of arrays, where each inner array represents a row.
   * @param filename The name of the file to be saved (e.g., 'leave-report.pdf').
   * @param title The title to be displayed at the top of the PDF.
   */
  exportToPdf(headers: string[], data: any[][], filename: string, title: string): void {
    const doc = new jsPDF();

    // Add a title to the document
    doc.text(title, 14, 15);

    // Use autoTable to generate the table from the data
    autoTable(doc, {
      startY: 20, // Start the table below the title
      head: [headers], // The headers row
      body: data,      // The data rows
      theme: 'striped',
      headStyles: {
        fillColor: [38, 50, 56] // A dark grey color for the header
      }
    });

    // Save the PDF
    doc.save(filename);
  }
}