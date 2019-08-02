package org.adelbs.iso8583.util;

import java.util.ArrayList;

public class AsciiTable {

	private ArrayList<AsciiTableItem> items = new ArrayList<AsciiTableItem>();
	
	public AsciiTable() {
		//ASCII control characters
		items.add(new AsciiTableItem((byte) 0, "00", "00000000", null, "null"));
		items.add(new AsciiTableItem((byte) 1, "01", "00000001", null, "start of header"));
		items.add(new AsciiTableItem((byte) 2, "02", "00000010", null, "start of text"));
		items.add(new AsciiTableItem((byte) 3, "03", "00000011", null, "end of text"));
		items.add(new AsciiTableItem((byte) 4, "04", "00000100", null, "end of transmission"));
		items.add(new AsciiTableItem((byte) 5, "05", "00000101", null, "enquiry"));
		items.add(new AsciiTableItem((byte) 6, "06", "00000110", null, "acknowledge"));
		items.add(new AsciiTableItem((byte) 7, "07", "00000111", null, "bell"));
		items.add(new AsciiTableItem((byte) 8, "08", "00001000", null, "backspace"));
		items.add(new AsciiTableItem((byte) 9, "09", "00001001", null, "horizontal tab"));
		items.add(new AsciiTableItem((byte) 10, "0A", "00001010", null, "line feed"));
		items.add(new AsciiTableItem((byte) 11, "0B", "00001011", null, "vertical tab"));
		items.add(new AsciiTableItem((byte) 12, "0C", "00001100", null, "form feed"));
		items.add(new AsciiTableItem((byte) 13, "0D", "00001101", null, "enter / carriage return"));
		items.add(new AsciiTableItem((byte) 14, "0E", "00001110", null, "shift out"));
		items.add(new AsciiTableItem((byte) 15, "0F", "00001111", null, "shift in"));
		items.add(new AsciiTableItem((byte) 16, "10", "00010000", null, "data link escape"));
		items.add(new AsciiTableItem((byte) 17, "11", "00010001", null, "device control 1"));
		items.add(new AsciiTableItem((byte) 18, "12", "00010010", null, "device control 2"));
		items.add(new AsciiTableItem((byte) 19, "13", "00010011", null, "device control 3"));
		items.add(new AsciiTableItem((byte) 20, "14", "00010100", null, "device control 4"));
		items.add(new AsciiTableItem((byte) 21, "15", "00010101", null, "negative acknowledge"));
		items.add(new AsciiTableItem((byte) 22, "16", "00010110", null, "synchronize"));
		items.add(new AsciiTableItem((byte) 23, "17", "00010111", null, "end of trans. block"));
		items.add(new AsciiTableItem((byte) 24, "18", "00011000", null, "cancel"));
		items.add(new AsciiTableItem((byte) 25, "19", "00011001", null, "end of medium"));
		items.add(new AsciiTableItem((byte) 26, "1A", "00011010", null, "substitute"));
		items.add(new AsciiTableItem((byte) 27, "1B", "00011011", null, "escape"));
		items.add(new AsciiTableItem((byte) 28, "1C", "00011100", null, "file separator"));
		items.add(new AsciiTableItem((byte) 29, "1D", "00011101", null, "group separator"));
		items.add(new AsciiTableItem((byte) 30, "1E", "00011110", null, "record separator"));
		items.add(new AsciiTableItem((byte) 31, "1F", "00011111", null, "unit separator"));
		items.add(new AsciiTableItem((byte) 127, "7F", "01111111", null, "delete"));

		//ASCII printable characters
		items.add(new AsciiTableItem((byte) 32, "20", "00100000", " ", "space"));
		items.add(new AsciiTableItem((byte) 33, "21", "00100001", "!", "exclamation mark"));
		items.add(new AsciiTableItem((byte) 34, "22", "00100010", "\"", "double quote"));
		items.add(new AsciiTableItem((byte) 35, "23", "00100011", "#", "number"));
		items.add(new AsciiTableItem((byte) 36, "24", "00100100", "$", "dollar"));
		items.add(new AsciiTableItem((byte) 37, "25", "00100101", "%", "percent"));
		items.add(new AsciiTableItem((byte) 38, "26", "00100110", "&", "ampersand"));
		items.add(new AsciiTableItem((byte) 39, "27", "00100111", "'", "single quote"));
		items.add(new AsciiTableItem((byte) 40, "28", "00101000", "(", "left parenthesis"));
		items.add(new AsciiTableItem((byte) 41, "29", "00101001", ")", "right parenthesis"));
		items.add(new AsciiTableItem((byte) 42, "2A", "00101010", "*", "asterisk"));
		items.add(new AsciiTableItem((byte) 43, "2B", "00101011", "+", "plus"));
		items.add(new AsciiTableItem((byte) 44, "2C", "00101100", ",", "comma"));
		items.add(new AsciiTableItem((byte) 45, "2D", "00101101", "-", "minus"));
		items.add(new AsciiTableItem((byte) 46, "2E", "00101110", ".", "period"));
		items.add(new AsciiTableItem((byte) 47, "2F", "00101111", "/", "slash"));
		items.add(new AsciiTableItem((byte) 48, "30", "00110000", "0", "zero"));
		items.add(new AsciiTableItem((byte) 49, "31", "00110001", "1", "one"));
		items.add(new AsciiTableItem((byte) 50, "32", "00110010", "2", "two"));
		items.add(new AsciiTableItem((byte) 51, "33", "00110011", "3", "three"));
		items.add(new AsciiTableItem((byte) 52, "34", "00110100", "4", "four"));
		items.add(new AsciiTableItem((byte) 53, "35", "00110101", "5", "five"));
		items.add(new AsciiTableItem((byte) 54, "36", "00110110", "6", "six"));
		items.add(new AsciiTableItem((byte) 55, "37", "00110111", "7", "seven"));
		items.add(new AsciiTableItem((byte) 56, "38", "00111000", "8", "eight"));
		items.add(new AsciiTableItem((byte) 57, "39", "00111001", "9", "nine"));
		items.add(new AsciiTableItem((byte) 58, "3A", "00111010", ":", "colon"));
		items.add(new AsciiTableItem((byte) 59, "3B", "00111011", ";", "semicolon"));
		items.add(new AsciiTableItem((byte) 60, "3C", "00111100", "<", "less than"));
		items.add(new AsciiTableItem((byte) 61, "3D", "00111101", "=", "equality sign"));
		items.add(new AsciiTableItem((byte) 62, "3E", "00111110", ">", "greater than"));
		items.add(new AsciiTableItem((byte) 63, "3F", "00111111", "?", "question mark"));
		items.add(new AsciiTableItem((byte) 64, "40", "01000000", "@", "at sign"));
		items.add(new AsciiTableItem((byte) 65, "41", "01000001", "A", " "));
		items.add(new AsciiTableItem((byte) 66, "42", "01000010", "B", " "));
		items.add(new AsciiTableItem((byte) 67, "43", "01000011", "C", " "));
		items.add(new AsciiTableItem((byte) 68, "44", "01000100", "D", " "));
		items.add(new AsciiTableItem((byte) 69, "45", "01000101", "E", " "));
		items.add(new AsciiTableItem((byte) 70, "46", "01000110", "F", " "));
		items.add(new AsciiTableItem((byte) 71, "47", "01000111", "G", " "));
		items.add(new AsciiTableItem((byte) 72, "48", "01001000", "H", " "));
		items.add(new AsciiTableItem((byte) 73, "49", "01001001", "I", " "));
		items.add(new AsciiTableItem((byte) 74, "4A", "01001010", "J", " "));
		items.add(new AsciiTableItem((byte) 75, "4B", "01001011", "K", " "));
		items.add(new AsciiTableItem((byte) 76, "4C", "01001100", "L", " "));
		items.add(new AsciiTableItem((byte) 77, "4D", "01001101", "M", " "));
		items.add(new AsciiTableItem((byte) 78, "4E", "01001110", "N", " "));
		items.add(new AsciiTableItem((byte) 79, "4F", "01001111", "O", " "));
		items.add(new AsciiTableItem((byte) 80, "50", "01010000", "P", " "));
		items.add(new AsciiTableItem((byte) 81, "51", "01010001", "Q", " "));
		items.add(new AsciiTableItem((byte) 82, "52", "01010010", "R", " "));
		items.add(new AsciiTableItem((byte) 83, "53", "01010011", "S", " "));
		items.add(new AsciiTableItem((byte) 84, "54", "01010100", "T", " "));
		items.add(new AsciiTableItem((byte) 85, "55", "01010101", "U", " "));
		items.add(new AsciiTableItem((byte) 86, "56", "01010110", "V", " "));
		items.add(new AsciiTableItem((byte) 87, "57", "01010111", "W", " "));
		items.add(new AsciiTableItem((byte) 88, "58", "01011000", "X", " "));
		items.add(new AsciiTableItem((byte) 89, "59", "01011001", "Y", " "));
		items.add(new AsciiTableItem((byte) 90, "5A", "01011010", "Z", " "));
		items.add(new AsciiTableItem((byte) 91, "5B", "01011011", "[", "left square bracket"));
		items.add(new AsciiTableItem((byte) 92, "5C", "01011100", "\\", "backslash"));
		items.add(new AsciiTableItem((byte) 93, "5D", "01011101", "]", "right square bracket"));
		items.add(new AsciiTableItem((byte) 94, "5E", "01011110", "^", "caret / circumflex"));
		items.add(new AsciiTableItem((byte) 95, "5F", "01011111", "_", "underscore"));
		items.add(new AsciiTableItem((byte) 96, "60", "01100000", "`", "grave / accent"));
		items.add(new AsciiTableItem((byte) 97, "61", "01100001", "a", " "));
		items.add(new AsciiTableItem((byte) 98, "62", "01100010", "b", " "));
		items.add(new AsciiTableItem((byte) 99, "63", "01100011", "c", " "));
		items.add(new AsciiTableItem((byte) 100, "64", "01100100", "d", " "));
		items.add(new AsciiTableItem((byte) 101, "65", "01100101", "e", " "));
		items.add(new AsciiTableItem((byte) 102, "66", "01100110", "f", " "));
		items.add(new AsciiTableItem((byte) 103, "67", "01100111", "g", " "));
		items.add(new AsciiTableItem((byte) 104, "68", "01101000", "h", " "));
		items.add(new AsciiTableItem((byte) 105, "69", "01101001", "i", " "));
		items.add(new AsciiTableItem((byte) 106, "6A", "01101010", "j", " "));
		items.add(new AsciiTableItem((byte) 107, "6B", "01101011", "k", " "));
		items.add(new AsciiTableItem((byte) 108, "6C", "01101100", "l", " "));
		items.add(new AsciiTableItem((byte) 109, "6D", "01101101", "m", " "));
		items.add(new AsciiTableItem((byte) 110, "6E", "01101110", "n", " "));
		items.add(new AsciiTableItem((byte) 111, "6F", "01101111", "o", " "));
		items.add(new AsciiTableItem((byte) 112, "70", "01110000", "p", " "));
		items.add(new AsciiTableItem((byte) 113, "71", "01110001", "q", " "));
		items.add(new AsciiTableItem((byte) 114, "72", "01110010", "r", " "));
		items.add(new AsciiTableItem((byte) 115, "73", "01110011", "s", " "));
		items.add(new AsciiTableItem((byte) 116, "74", "01110100", "t", " "));
		items.add(new AsciiTableItem((byte) 117, "75", "01110101", "u", " "));
		items.add(new AsciiTableItem((byte) 118, "76", "01110110", "v", " "));
		items.add(new AsciiTableItem((byte) 119, "77", "01110111", "w", " "));
		items.add(new AsciiTableItem((byte) 120, "78", "01111000", "x", " "));
		items.add(new AsciiTableItem((byte) 121, "79", "01111001", "y", " "));
		items.add(new AsciiTableItem((byte) 122, "7A", "01111010", "z", " "));
		items.add(new AsciiTableItem((byte) 123, "7B", "01111011", "{", "left curly bracket"));
		items.add(new AsciiTableItem((byte) 124, "7C", "01111100", "|", "vertical bar"));
		items.add(new AsciiTableItem((byte) 125, "7D", "01111101", "}", "right curly bracket"));
		items.add(new AsciiTableItem((byte) 126, "7E", "01111110", "~", "tilde"));
		items.add(new AsciiTableItem((byte) 127, "7F", "01111111", null, "delete"));

		//Extended ASCII characters
		items.add(new AsciiTableItem((byte) 128, "80", "10000000", null, " "));
		items.add(new AsciiTableItem((byte) 129, "81", "10000001", null, " "));
		items.add(new AsciiTableItem((byte) 130, "82", "10000010", null, " "));
		items.add(new AsciiTableItem((byte) 131, "83", "10000011", null, " "));
		items.add(new AsciiTableItem((byte) 132, "84", "10000100", null, " "));
		items.add(new AsciiTableItem((byte) 133, "85", "10000101", null, " "));
		items.add(new AsciiTableItem((byte) 134, "86", "10000110", null, " "));
		items.add(new AsciiTableItem((byte) 135, "87", "10000111", null, " "));
		items.add(new AsciiTableItem((byte) 136, "88", "10001000", null, " "));
		items.add(new AsciiTableItem((byte) 137, "89", "10001001", null, " "));
		items.add(new AsciiTableItem((byte) 138, "8A", "10001010", null, " "));
		items.add(new AsciiTableItem((byte) 139, "8B", "10001011", null, " "));
		items.add(new AsciiTableItem((byte) 140, "8C", "10001100", null, " "));
		items.add(new AsciiTableItem((byte) 141, "8D", "10001101", null, " "));
		items.add(new AsciiTableItem((byte) 142, "8E", "10001110", null, " "));
		items.add(new AsciiTableItem((byte) 143, "8F", "10001111", null, " "));
		items.add(new AsciiTableItem((byte) 144, "90", "10010000", null, " "));
		items.add(new AsciiTableItem((byte) 145, "91", "10010001", null, " "));
		items.add(new AsciiTableItem((byte) 146, "92", "10010010", null, " "));
		items.add(new AsciiTableItem((byte) 147, "93", "10010011", null, " "));
		items.add(new AsciiTableItem((byte) 148, "94", "10010100", null, " "));
		items.add(new AsciiTableItem((byte) 149, "95", "10010101", null, " "));
		items.add(new AsciiTableItem((byte) 150, "96", "10010110", null, " "));
		items.add(new AsciiTableItem((byte) 151, "97", "10010111", null, " "));
		items.add(new AsciiTableItem((byte) 152, "98", "10011000", null, " "));
		items.add(new AsciiTableItem((byte) 153, "99", "10011001", null, " "));
		items.add(new AsciiTableItem((byte) 154, "9A", "10011010", null, " "));
		items.add(new AsciiTableItem((byte) 155, "9B", "10011011", null, " "));
		items.add(new AsciiTableItem((byte) 156, "9C", "10011100", null, " "));
		items.add(new AsciiTableItem((byte) 157, "9D", "10011101", null, " "));
		items.add(new AsciiTableItem((byte) 158, "9E", "10011110", null, " "));
		items.add(new AsciiTableItem((byte) 159, "9F", "10011111", null, " "));
		items.add(new AsciiTableItem((byte) 160, "A0", "10100000", " ", "space"));
		items.add(new AsciiTableItem((byte) 161, "A1", "10100001", "¡", " "));
		items.add(new AsciiTableItem((byte) 162, "A2", "10100010", "¢", "cent"));
		items.add(new AsciiTableItem((byte) 163, "A3", "10100011", "£", "pound"));
		items.add(new AsciiTableItem((byte) 164, "A4", "10100100", "¤", "currency sign"));
		items.add(new AsciiTableItem((byte) 165, "A5", "10100101", "¥", "yen, yuan"));
		items.add(new AsciiTableItem((byte) 166, "A6", "10100110", "¦", "broken bar"));
		items.add(new AsciiTableItem((byte) 167, "A7", "10100111", "§", "section sign"));
		items.add(new AsciiTableItem((byte) 168, "A8", "10101000", "¨", " "));
		items.add(new AsciiTableItem((byte) 169, "A9", "10101001", "©", "copyright"));
		items.add(new AsciiTableItem((byte) 170, "AA", "10101010", "ª", "ordinal indicator"));
		items.add(new AsciiTableItem((byte) 171, "AB", "10101011", "«", " "));
		items.add(new AsciiTableItem((byte) 172, "AC", "10101100", "¬", " "));
		items.add(new AsciiTableItem((byte) 173, "AD", "10101101", null, " "));
		items.add(new AsciiTableItem((byte) 174, "AE", "10101110", "®", "registered trademark"));
		items.add(new AsciiTableItem((byte) 175, "AF", "10101111", "¯", " "));
		items.add(new AsciiTableItem((byte) 176, "B0", "10110000", "°", "degree"));
		items.add(new AsciiTableItem((byte) 177, "B1", "10110001", "±", "plus-minus"));
		items.add(new AsciiTableItem((byte) 178, "B2", "10110010", "²", " "));
		items.add(new AsciiTableItem((byte) 179, "B3", "10110011", "³", " "));
		items.add(new AsciiTableItem((byte) 180, "B4", "10110100", "´", " "));
		items.add(new AsciiTableItem((byte) 181, "B5", "10110101", "µ", "mu"));
		items.add(new AsciiTableItem((byte) 182, "B6", "10110110", "¶", "pilcrow"));
		items.add(new AsciiTableItem((byte) 183, "B7", "10110111", "·", " "));
		items.add(new AsciiTableItem((byte) 184, "B8", "10111000", "¸", " "));
		items.add(new AsciiTableItem((byte) 185, "B9", "10111001", "¹", " "));
		items.add(new AsciiTableItem((byte) 186, "BA", "10111010", "º", "ordinal indicator"));
		items.add(new AsciiTableItem((byte) 187, "BB", "10111011", "»", " "));
		items.add(new AsciiTableItem((byte) 188, "BC", "10111100", "¼", " "));
		items.add(new AsciiTableItem((byte) 189, "BD", "10111101", "½", " "));
		items.add(new AsciiTableItem((byte) 190, "BE", "10111110", "¾", " "));
		items.add(new AsciiTableItem((byte) 191, "BF", "10111111", "¿", "inverted question mark"));
		items.add(new AsciiTableItem((byte) 192, "C0", "11000000", "À", " "));
		items.add(new AsciiTableItem((byte) 193, "C1", "11000001", "Á", " "));
		items.add(new AsciiTableItem((byte) 194, "C2", "11000010", "Â", " "));
		items.add(new AsciiTableItem((byte) 195, "C3", "11000011", "Ã", " "));
		items.add(new AsciiTableItem((byte) 196, "C4", "11000100", "Ä", " "));
		items.add(new AsciiTableItem((byte) 197, "C5", "11000101", "Å", " "));
		items.add(new AsciiTableItem((byte) 198, "C6", "11000110", "Æ", " "));
		items.add(new AsciiTableItem((byte) 199, "C7", "11000111", "Ç", " "));
		items.add(new AsciiTableItem((byte) 200, "C8", "11001000", "È", " "));
		items.add(new AsciiTableItem((byte) 201, "C9", "11001001", "É", " "));
		items.add(new AsciiTableItem((byte) 202, "CA", "11001010", "Ê", " "));
		items.add(new AsciiTableItem((byte) 203, "CB", "11001011", "Ë", " "));
		items.add(new AsciiTableItem((byte) 204, "CC", "11001100", "Ì", " "));
		items.add(new AsciiTableItem((byte) 205, "CD", "11001101", "Í", " "));
		items.add(new AsciiTableItem((byte) 206, "CE", "11001110", "Î", " "));
		items.add(new AsciiTableItem((byte) 207, "CF", "11001111", "Ï", " "));
		items.add(new AsciiTableItem((byte) 208, "D0", "11010000", "Ð", " "));
		items.add(new AsciiTableItem((byte) 209, "D1", "11010001", "Ñ", " "));
		items.add(new AsciiTableItem((byte) 210, "D2", "11010010", "Ò", " "));
		items.add(new AsciiTableItem((byte) 211, "D3", "11010011", "Ó", " "));
		items.add(new AsciiTableItem((byte) 212, "D4", "11010100", "Ô", " "));
		items.add(new AsciiTableItem((byte) 213, "D5", "11010101", "Õ", " "));
		items.add(new AsciiTableItem((byte) 214, "D6", "11010110", "Ö", " "));
		items.add(new AsciiTableItem((byte) 215, "D7", "11010111", "×", "multiplication sign"));
		items.add(new AsciiTableItem((byte) 216, "D8", "11011000", "Ø", " "));
		items.add(new AsciiTableItem((byte) 217, "D9", "11011001", "Ù", " "));
		items.add(new AsciiTableItem((byte) 218, "DA", "11011010", "Ú", " "));
		items.add(new AsciiTableItem((byte) 219, "DB", "11011011", "Û", " "));
		items.add(new AsciiTableItem((byte) 220, "DC", "11011100", "Ü", " "));
		items.add(new AsciiTableItem((byte) 221, "DD", "11011101", "Ý", " "));
		items.add(new AsciiTableItem((byte) 222, "DE", "11011110", "Þ", " "));
		items.add(new AsciiTableItem((byte) 223, "DF", "11011111", "ß", " "));
		items.add(new AsciiTableItem((byte) 224, "E0", "11100000", "à", " "));
		items.add(new AsciiTableItem((byte) 225, "E1", "11100001", "á", " "));
		items.add(new AsciiTableItem((byte) 226, "E2", "11100010", "â", " "));
		items.add(new AsciiTableItem((byte) 227, "E3", "11100011", "ã", " "));
		items.add(new AsciiTableItem((byte) 228, "E4", "11100100", "ä", " "));
		items.add(new AsciiTableItem((byte) 229, "E5", "11100101", "å", " "));
		items.add(new AsciiTableItem((byte) 230, "E6", "11100110", "æ", " "));
		items.add(new AsciiTableItem((byte) 231, "E7", "11100111", "ç", " "));
		items.add(new AsciiTableItem((byte) 232, "E8", "11101000", "è", " "));
		items.add(new AsciiTableItem((byte) 233, "E9", "11101001", "é", " "));
		items.add(new AsciiTableItem((byte) 234, "EA", "11101010", "ê", " "));
		items.add(new AsciiTableItem((byte) 235, "EB", "11101011", "ë", " "));
		items.add(new AsciiTableItem((byte) 236, "EC", "11101100", "ì", " "));
		items.add(new AsciiTableItem((byte) 237, "ED", "11101101", "í", " "));
		items.add(new AsciiTableItem((byte) 238, "EE", "11101110", "î", " "));
		items.add(new AsciiTableItem((byte) 239, "EF", "11101111", "ï", " "));
		items.add(new AsciiTableItem((byte) 240, "F0", "11110000", "ð", " "));
		items.add(new AsciiTableItem((byte) 241, "F1", "11110001", "ñ", " "));
		items.add(new AsciiTableItem((byte) 242, "F2", "11110010", "ò", " "));
		items.add(new AsciiTableItem((byte) 243, "F3", "11110011", "ó", " "));
		items.add(new AsciiTableItem((byte) 244, "F4", "11110100", "ô", " "));
		items.add(new AsciiTableItem((byte) 245, "F5", "11110101", "õ", " "));
		items.add(new AsciiTableItem((byte) 246, "F6", "11110110", "ö", " "));
		items.add(new AsciiTableItem((byte) 247, "F7", "11110111", "÷", "obelus"));
		items.add(new AsciiTableItem((byte) 248, "F8", "11111000", "ø", " "));
		items.add(new AsciiTableItem((byte) 249, "F9", "11111001", "ù", " "));
		items.add(new AsciiTableItem((byte) 250, "FA", "11111010", "ú", " "));
		items.add(new AsciiTableItem((byte) 251, "FB", "11111011", "û", " "));
		items.add(new AsciiTableItem((byte) 252, "FC", "11111100", "ü", " "));
		items.add(new AsciiTableItem((byte) 253, "FD", "11111101", "ý", " "));
		items.add(new AsciiTableItem((byte) 254, "FE", "11111110", "þ", " "));
		items.add(new AsciiTableItem((byte) 255, "FF", "11111111", "ÿ", " "));
	}
	
	public byte findByteFromBinary(String binary) {
		byte result = -1;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getBinary().equalsIgnoreCase(binary)) {
				result = items.get(i).getDecimal();
				break;
			}
		}
		
		return result;
	}
	
	public String findBinaryFromByte(byte decimal) {
		String result = null;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getDecimal() == decimal) {
				result = items.get(i).getBinary();
				break;				
			}
		}
		
		return result;
	}
	
	public byte findDecimalFromHexa(String hexa) {
		byte result = -1;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getHexa().equalsIgnoreCase(hexa)) {
				result = items.get(i).getDecimal();
				break;
			}
		}
		
		return result;		
	}
	
	public String findHexaFromByte(byte decimal) {
		String result = null;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getDecimal() == decimal) {
				result = items.get(i).getHexa();
				break;
			}
		}
		
		return result;				
	}
	
}
