//Myron Woods
//Deliverable 3
//May 1, 2018
//Lexical Scanner, Parser, and interpreter

import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LexicalAnalyzer {


   static interpreter t = new interpreter();

   private List<Token> tokenList;
   public static TokenType[][] parser = new TokenType[20][20];
   
   /*Exception for lexical analysis*/
   public class LexicalException extends Exception {
   
      public LexicalException(String message) {
         super(message);
      }
   
   }
   /* An enumeration that creates a special data type for tokens*/
   public enum TokenType {
      LEFT_PAREN_TOK, RIGHT_PAREN_TOK, IF_TOK, THEN_TOK,
      ELSE_TOK, WHILE_TOK, ID_TOK, PRINT_TOK, GE_TOK, GT_TOK, 
      LE_TOK, LT_TOK, EQ_TOK, NE_TOK, ADD_TOK, SUB_TOK, MUL_TOK,
      DIV_TOK, ASSIGN_TOK, EOS_TOK, LITERAL_INTEGER_TOK, MATH_TOK,
      COMP_TOK, LEFT_BRAK_TOK, RIGHT_BRAK_TOK
   }
   
   /* This token classes creates variables that will be used to extract tokens and also
   to give an exact loction if a string of chracters is not in the given set of tokens.*/
  
   public class Token {
   
      private LexicalAnalyzer.TokenType tokType;
      private String lexeme;
      private int rowNumber;
      private int columnNumber;
   
   /*This is a constructor of the token class. This constructor takes a file input
   and checks to see if it violates any rules of the scanner and then sets the variables
   in the data field*/
      public Token(LexicalAnalyzer.TokenType tokType, String lexeme, int rowNumber,
              int columnNumber) {
         if (tokType == null) {
            throw new IllegalArgumentException("null TokenType");
         }
         if (lexeme == null || lexeme.length() == 0) {
            throw new IllegalArgumentException("invalid lexeme");
         }
         if (rowNumber <= 0) {
            throw new IllegalArgumentException("invalid row number");
         }
         if (columnNumber <= 0) {
            throw new IllegalArgumentException("invalid column number");
         }
         this.tokType = tokType;
         this.lexeme = lexeme;
         this.rowNumber = rowNumber;
         this.columnNumber = columnNumber;
      }
   /*Series of getters used in other methods*/
      public LexicalAnalyzer.TokenType getTokType() {
         return tokType;
      }
   
      public String getLexeme() {
         return lexeme;
      }
   
      public int getRowNumber() {
         return rowNumber;
      }
   
      public int getColumnNumber() {
         return columnNumber;
      }
   
   }
   

   /*reads the input file and invokes the "processLine" method.*/
   public LexicalAnalyzer(String fileName) throws FileNotFoundException, LexicalException {
      if (fileName == null) {
         throw new IllegalArgumentException("null file name argument");
      }
      tokenList = new ArrayList<Token>();
      Scanner input = new Scanner(new File(fileName));
      int lineNumber = 0;
      while (input.hasNext()) {
         String line = input.nextLine();
         lineNumber++;
         processLine(line, lineNumber);
         if (t.repeat==true){
            input=new Scanner(new File(fileName));
            for(int j=0; j< t.line;j++){input.nextLine();}
            for(int i=t.line; i<20;i++){
               for(int k=0; k<20;k++){parser[i][k]=null;}}
            lineNumber=t.line;
            t.repeat=false;}
      }
      input.close();
      tokenList.add(new Token(TokenType.EOS_TOK, "EOS", lineNumber, 1));
   }

   /* checks processes on each line and collects all the tokens and calls the printOutput to print to console*/
   private void processLine(String line, int lineNumber) throws LexicalAnalyzer.LexicalException {
      if (line == null) {
         throw new IllegalArgumentException("no lines to be processed");
      }
      if (lineNumber <= 0) {
         throw new IllegalArgumentException("invalid line number");
      }
      int index = skipWhiteSpace(line, 0);
      while (index < line.length()) {
         String lexeme = getLexeme(line, index);
         LexicalAnalyzer.TokenType tokType = getTokenType(lexeme, lineNumber, index + 1);
         tokenList.add(new LexicalAnalyzer.Token(tokType, lexeme, lineNumber, index + 1));
         index += lexeme.length();
         index = skipWhiteSpace(line, index);
         printOutput(lexeme, lineNumber, tokType);// print the output            
      }
   }

   /* gets the token type, uses a switch to check all the key words*/
   private LexicalAnalyzer.TokenType getTokenType(String lexeme, int rowNumber, int columnNumber) throws LexicalAnalyzer.LexicalException {
      if (lexeme == null || lexeme.length() == 0) {
         throw new IllegalArgumentException("invalid string argument");
      }
      LexicalAnalyzer.TokenType tokType = LexicalAnalyzer.TokenType.EOS_TOK;
      if (Character.isDigit(lexeme.charAt(0))) {
         if (allDigits(lexeme)) {
            tokType = LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK;
            
            int a = (int)lexeme.charAt(0);
            
            LexicalAnalyzer.t.checkValueStatus(a-48);
         } 
         else {
            throw new LexicalAnalyzer.LexicalException("literal integer expecated " + " at row "
                   + rowNumber + " and column " + columnNumber);
         }
      } 
      else if (Character.isLetter(lexeme.charAt(0))) {
         if (lexeme.length() == 1) {
            tokType = LexicalAnalyzer.TokenType.ID_TOK;
            LexicalAnalyzer.t.checkVariableStatus(lexeme.charAt(0));
         } 
         else if (lexeme.equals("if")) {
            tokType = LexicalAnalyzer.TokenType.IF_TOK;
         }  
         else if (lexeme.equals("then")) {
            tokType = LexicalAnalyzer.TokenType.THEN_TOK;
         }  
         else if (lexeme.equals("else")) {
            tokType = LexicalAnalyzer.TokenType.ELSE_TOK;
         } 
         else if (lexeme.equals("while")) {
            tokType = LexicalAnalyzer.TokenType.WHILE_TOK;
         } 
         else if (lexeme.equals("print")) {
            tokType = LexicalAnalyzer.TokenType.PRINT_TOK;
         } 
         else {
            throw new LexicalAnalyzer.LexicalException("invalid lexeme " + " at row "
                   + rowNumber + " and column " + columnNumber);
         }
      } 
      else if (lexeme.equals("(")) {
         tokType = LexicalAnalyzer.TokenType.LEFT_PAREN_TOK;
      } 
      else if (lexeme.equals(")")) {
         tokType = LexicalAnalyzer.TokenType.RIGHT_PAREN_TOK;
      } 
      else if (lexeme.equals("{")) {
         tokType = LexicalAnalyzer.TokenType.LEFT_BRAK_TOK;
      } 
      else if (lexeme.equals("}")) {
         tokType = LexicalAnalyzer.TokenType.RIGHT_BRAK_TOK;
      }
      else if (lexeme.equals(">=")) {
         tokType = LexicalAnalyzer.TokenType.GE_TOK;
      } 
      else if (lexeme.equals(">")) {
         tokType = LexicalAnalyzer.TokenType.GT_TOK;
      } 
      else if (lexeme.equals("<=")) {
         tokType = LexicalAnalyzer.TokenType.LE_TOK;
      } 
      else if (lexeme.equals("<")) {
         tokType = LexicalAnalyzer.TokenType.LT_TOK;
         LexicalAnalyzer.t.checkRelationStatus(2);
      } 
      else if (lexeme.equals("==")) {
         tokType = LexicalAnalyzer.TokenType.EQ_TOK;
      } 
      else if (lexeme.equals("~=")) {
         tokType = LexicalAnalyzer.TokenType.NE_TOK;
      } 
      else if (lexeme.equals("+")) {
         tokType = LexicalAnalyzer.TokenType.ADD_TOK;
      } 
      else if (lexeme.equals("-")) {
         tokType = LexicalAnalyzer.TokenType.SUB_TOK;
      } 
      else if (lexeme.equals("*")) {
         tokType = LexicalAnalyzer.TokenType.MUL_TOK;
      } 
      else if (lexeme.equals("/")) {
         tokType = LexicalAnalyzer.TokenType.DIV_TOK;
      } 
      else if (lexeme.equals("=")) {
         tokType = LexicalAnalyzer.TokenType.ASSIGN_TOK;
         LexicalAnalyzer.t.checkRelationStatus(1);
         
      } 
      else {
         throw new LexicalAnalyzer.LexicalException("invalid lexeme " + " at row "
                + rowNumber + " and column " + columnNumber);
      }
      return tokType;
   }

   /*checks if the collected lexeme only contains numbers*/ 
   private boolean allDigits(String lexeme) {
      if (lexeme == null) {
         throw new IllegalArgumentException("null string argument");
      }
      int i = 0;
      while (i < lexeme.length() && Character.isDigit(lexeme.charAt(i))) {
         i++;
      }
      return i == lexeme.length();
   }

   /*uses the getter in token to retrieve the lexeme so that it can be added to the token list*/
   private String getLexeme(String line, int index) {
      if (line == null) {
         throw new IllegalArgumentException("null string argument");
      }
      if (index < 0) {
         throw new IllegalArgumentException("invalid index argument");
      }
      int i = index;
      while (i < line.length() && !Character.isWhitespace(line.charAt(i))) {
         i++;
      }
      return line.substring(index, i);
   }

   /*skips all the whitespaces so that it can collect only the tokens and ignore the whitespace*/
   private int skipWhiteSpace(String line, int index) {
      while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
         index++;
      }
      return index;
   }

   // makes sure 
   public LexicalAnalyzer.Token getLookaheadToken() throws LexicalAnalyzer.LexicalException {
      if (tokenList.isEmpty()) {
         throw new LexicalAnalyzer.LexicalException("no more tokens");
      }
      return tokenList.get(0);
   }

   /* checks if there are more tokens to be read by the scanner*/
   public LexicalAnalyzer.Token getNextToken() throws LexicalAnalyzer.LexicalException {
      if (tokenList.isEmpty()) {
         throw new LexicalAnalyzer.LexicalException("no more tokens");
      }
      return tokenList.remove(0);
   }

   /* prints the output to the console*/
   private void printOutput(String lex, int num, TokenType ttype) {
      System.out.printf(" Next token is |" + ttype + "|");
      System.out.printf(" and the lexeme |" + lex + "|");
      System.out.printf(" which is found on line number|" + num + "|");
      System.out.println("\n");
      
      int a = 0;
      
    /*this load classifies each token and then loads them into a 
    multideminsional array.*/  
      TokenType[][] par = new TokenType[20][20];
      
      while(parser[num-1][a]!=null){a++;}
       
      if(ttype== LexicalAnalyzer.TokenType.ID_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.ID_TOK;}
      
      if(ttype== LexicalAnalyzer.TokenType.ADD_TOK){LexicalAnalyzer.t.checkValueStatus(-1);}
      if(ttype== LexicalAnalyzer.TokenType.SUB_TOK){LexicalAnalyzer.t.checkValueStatus(-2);}
      if(ttype== LexicalAnalyzer.TokenType.MUL_TOK){LexicalAnalyzer.t.checkValueStatus(-3);}
      if(ttype== LexicalAnalyzer.TokenType.DIV_TOK){LexicalAnalyzer.t.checkValueStatus(-4);}
      if(ttype== LexicalAnalyzer.TokenType.RIGHT_PAREN_TOK){
         t.evaluate(num-1);}
      
      if(ttype== LexicalAnalyzer.TokenType.RIGHT_BRAK_TOK){
         int v=num-1;
         
         while(parser[v][0]!= LexicalAnalyzer.TokenType.WHILE_TOK&&parser[v][0]!= LexicalAnalyzer.TokenType.IF_TOK&&t.exe[v]!=1){

            v--;}
         if(parser[v][0]==LexicalAnalyzer.TokenType.WHILE_TOK){
            t.line=v;
            t.conditional(v,(num-1-v));}
         if(parser[v][0]==LexicalAnalyzer.TokenType.IF_TOK){
            t.ifcondition(v,(num-1-v));}
      }
      if(ttype== LexicalAnalyzer.TokenType.ADD_TOK||
      ttype== LexicalAnalyzer.TokenType.SUB_TOK||
      ttype== LexicalAnalyzer.TokenType.MUL_TOK||
      ttype== LexicalAnalyzer.TokenType.DIV_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.MATH_TOK;} 
      
      if(ttype== LexicalAnalyzer.TokenType.GE_TOK||
      ttype== LexicalAnalyzer.TokenType.GT_TOK||
      ttype== LexicalAnalyzer.TokenType.LE_TOK||
      ttype== LexicalAnalyzer.TokenType.LT_TOK||
      ttype== LexicalAnalyzer.TokenType.EQ_TOK||
      ttype== LexicalAnalyzer.TokenType.NE_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.COMP_TOK;} 
      
      if(ttype==LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK;}
      
      if(ttype==LexicalAnalyzer.TokenType.ASSIGN_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.ASSIGN_TOK;}
      
      if(ttype==LexicalAnalyzer.TokenType.IF_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.IF_TOK;}  
        
      if(ttype==LexicalAnalyzer.TokenType.WHILE_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.WHILE_TOK;}
      if(ttype==LexicalAnalyzer.TokenType.PRINT_TOK){
         par[num-1][a]=LexicalAnalyzer.TokenType.PRINT_TOK;
         t.print[num-1]=-3;}      
      
      parser[num-1][a]=par[num-1][a];
      
   }
   //prints the array holding the tokens
   public static void print(TokenType[][]parse){
   
      for(int i = 0; i < 20; i++)
      {
         for(int j = 0; j < 20; j++)
         {
            if(parse[i][j]!=null){
               System.out.print(parse[i][j]+", ");}
         }
         if(parse[i+1][0]==null){
            break;}
         System.out.println();
      }
   }
// parser checks for errors. 
   public static boolean Syntax(TokenType[][]parse){
      boolean error = false;
      for(int i = 0; i < 20; i++)
      {
         for(int j = 0; j < 19; j++)
         {
            if(parse[i][j]!=null){
               if(parse[i][j]== parse[i][j+1]||
               (parse[i][j]== LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.ID_TOK)||
               (parse[i][j]== LexicalAnalyzer.TokenType.ID_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK)||
               (parse[i][j]== LexicalAnalyzer.TokenType.COMP_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.MATH_TOK)){
                  System.out.print("ERROR ON LINE "+(i+1)+": "+parse[i][j]+" AND "+parse[i][j+1]+
                     " CANNOT BE NEXT TO EACH OTHER");
                  error=true; 
                  System.out.println();}     
            }
            if(parse[i][j]== LexicalAnalyzer.TokenType.MATH_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.COMP_TOK){
               System.out.print("ERROR ON LINE "+(i+1)+": CANNOT DO MATH ON COMPARISON TOKEN");
               error=true; 
               System.out.println();}
            if(parse[i][j]== LexicalAnalyzer.TokenType.ASSIGN_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.COMP_TOK){
               System.out.print("ERROR ON LINE "+(i+1)+": CANNOT ASSIGN COMPARISON TOKEN");
               error=true; 
               System.out.println();}
            if(parse[i][j]== LexicalAnalyzer.TokenType.ASSIGN_TOK&&
               parse[i][j+1]== LexicalAnalyzer.TokenType.MATH_TOK){
               System.out.print("ERROR ON LINE "+(i+1)+": CANNOT ASSIGN MATH TOKEN");
               error=true; 
               System.out.println();}
            if(parse[i][j]== LexicalAnalyzer.TokenType.ASSIGN_TOK){
               for(int k = j-1; k>= 0; k--){
                  if(parse[i][k]==LexicalAnalyzer.TokenType.COMP_TOK||
                  parse[i][k]==LexicalAnalyzer.TokenType.MATH_TOK||
                  parse[i][k]==LexicalAnalyzer.TokenType.LITERAL_INTEGER_TOK){
                     System.out.print("ERROR ON LINE "+(i+1)+": CANNOT ASSIGN TO PREVIOUS STATEMENT");
                     error=true; 
                     System.out.println();
                     break;}
               }
            } 
            if(parse[i][j]== LexicalAnalyzer.TokenType.COMP_TOK){
               for(int k = j-1; k>= 0; k--){
                  if(parse[i][k]==LexicalAnalyzer.TokenType.ASSIGN_TOK){
                     System.out.print("ERROR ON LINE "+(i+1)+": CANNOT COMPARE PREVIOUS STATEMENT");
                     error=true; 
                     System.out.println();
                     break;}
               }
            }  
            if((parse[i][j]== LexicalAnalyzer.TokenType.ASSIGN_TOK&&
               parse[i][j+1]== null)||
               (parse[i][j]== LexicalAnalyzer.TokenType.MATH_TOK&&
               parse[i][j+1]== null)||
               (parse[i][j]== LexicalAnalyzer.TokenType.COMP_TOK&&
               parse[i][j+1]== null)
               ){
               System.out.print("ERROR ON LINE "+(i+1)+": CANNOT END STATEMENT WITH "+parse[i][j]);
               error=true; 
               System.out.println();}       
         }
         if((parse[i][0]== LexicalAnalyzer.TokenType.MATH_TOK ||
               parse[i][0]== LexicalAnalyzer.TokenType.COMP_TOK)){
            System.out.print("ERROR ON LINE "+(i+1)+": CANNOT START LINE WITH "+parse[i][0]+" TOKEN");
            System.out.println();
            error=true;}      
         
         
      }
      
     
      if(error==false){
         System.out.println("PROGRAM EXECUTED");
         
         for(int i = 0; i < 20; i++){
            if(parse[i][0]== LexicalAnalyzer.TokenType.PRINT_TOK&&
            parse[i][1]== LexicalAnalyzer.TokenType.ID_TOK){
               t.printstatement(i);}
         }
      }
      return error;
      
   }
   

   /* main method the reads the text file*/
   public static void main(String args[]) throws FileNotFoundException, LexicalException {
      LexicalAnalyzer lexical = new LexicalAnalyzer("/Users/Myron/Documents/test.txt");
      print(parser);
      System.out.println();
      System.out.println();
      Syntax(parser);
      
     
   }
   
  
}
