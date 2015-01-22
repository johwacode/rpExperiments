package rpEngine.graphical.objects2d.text;


public class Font {
	public static boolean fontLoaded = false;
	
	public static void init_calibri_german(){
		CharacterLookUp.setTexture("text", 763, 178);
		CharacterLookUp.addCharacter('#', 536, 45, 16, 42);
		CharacterLookUp.addCharacter(' ', 684, 72, 16, 42);
		CharacterLookUp.addCharacter('A', 4, 5, 18, 42);
		CharacterLookUp.addCharacter('B', 25, 5, 15, 42);
		CharacterLookUp.addCharacter('C', 44, 5, 14, 42);
		CharacterLookUp.addCharacter('D', 63, 5, 17, 42);
		CharacterLookUp.addCharacter('E', 84, 5, 16, 42);
		CharacterLookUp.addCharacter('F', 100, 5, 16, 42);
		CharacterLookUp.addCharacter('G', 116, 5, 23, 42);
		CharacterLookUp.addCharacter('H', 137, 5, 23, 42);
		CharacterLookUp.addCharacter('I', 155, 5, 12, 42);
		CharacterLookUp.addCharacter('J', 166, 5, 13, 42);
		CharacterLookUp.addCharacter('K', 180, 5, 16, 42);
		CharacterLookUp.addCharacter('L', 196, 5, 16, 42);
		CharacterLookUp.addCharacter('M', 214, 5, 23, 42);
		CharacterLookUp.addCharacter('N', 238, 5, 21, 42);
		CharacterLookUp.addCharacter('O', 262, 5, 23, 42);
		CharacterLookUp.addCharacter('P', 286, 5, 19, 42);
		CharacterLookUp.addCharacter('Q', 306, 5, 20, 42);
		CharacterLookUp.addCharacter('R', 328, 5, 18, 42);
		CharacterLookUp.addCharacter('S', 347, 5, 14, 42);
		CharacterLookUp.addCharacter('T', 363, 5, 16, 42);
		CharacterLookUp.addCharacter('U', 380, 5, 19, 42);
		CharacterLookUp.addCharacter('V', 399, 5, 20, 42);
		CharacterLookUp.addCharacter('W', 420, 5, 30, 42);
		CharacterLookUp.addCharacter('X', 450, 5, 18, 42);
		CharacterLookUp.addCharacter('Y', 468, 5, 18, 42);
		CharacterLookUp.addCharacter('Z', 488, 5, 15, 42);
		
		CharacterLookUp.addCharacter('1', 3, 84, 16, 42);
		CharacterLookUp.addCharacter('2', 22, 84, 14, 42);
		CharacterLookUp.addCharacter('3', 38, 84, 16, 42);
		CharacterLookUp.addCharacter('4', 55, 84, 16, 42);
		CharacterLookUp.addCharacter('5', 75, 84, 15, 42);
		CharacterLookUp.addCharacter('6', 92, 84, 16, 42);
		CharacterLookUp.addCharacter('7', 110, 84, 16, 42);
		CharacterLookUp.addCharacter('8', 128, 84, 16, 42);
		CharacterLookUp.addCharacter('9', 145, 84, 16, 42);
		CharacterLookUp.addCharacter('0', 162, 84, 16, 42);
		
		CharacterLookUp.addCharacter(',', 218, 84, 5, 42);
		CharacterLookUp.addCharacter('.', 209, 84, 5, 42);
		CharacterLookUp.addCharacter(':', 355, 84, 5, 42);
		CharacterLookUp.addCharacter('>', 382, 84, 15, 42);
		CharacterLookUp.addCharacter('-', 500, 45, 10, 42);
		CharacterLookUp.addCharacter('+', 493, 84, 16, 42);
		fontLoaded = true;
	}
}
