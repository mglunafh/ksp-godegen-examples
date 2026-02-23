# CSV builder generator

Adds annotation `@CsvBuilder` for data classes which instructs KSP processor to generate:
- extension function `toCsvString()` -- CSV representation of a target class instance
- extension function `writeCsv()` on a collection which writes it to a specified destination
