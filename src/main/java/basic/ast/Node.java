/*************************************
 * Basic-JC կոմպիլյատոր
 *************************************/

package basic.ast;

/**
 * {@code Node} վերացական դասը <em>աբստրակտ քերականական ծառի</em> բոլոր
 * դասերի հիմնային դասն է։
 *
 * @author Արմեն Բադալյան
 */
public abstract class Node {
	/**
	 * Բեյսիկ-Փ լեզվի տարրական տիպերը։
	 */
	public enum Type {
		/**
		 * Կրկնակի ճշտության իրական թիվ։
		 */
		Real,
		/**
		 * Տեքստ։
		 */
		Text,
		/**
		 * Տրամաբանական արժեք։
		 */
		Logic;

		/**
		 * Որոշում է իդենտիպիկատորի տիպն ըստ վերջավորության։
		 *
		 * @param nm Իդենտիֆիկատոր։ Ենթածրագրի կամ փոփոխականի անուն։
		 *
		 * @return Վերադարձնում է {@code Type} թվարկման համապատասխան տարրը։
		 */
		public static final Type of( String nm )
		{
			char c = nm.charAt(nm.length()-1);
			if( c == '$' )
				return Type.Text;
			if( c == '?' )
				return Type.Logic;
			if( c == '#' )
				return Type.Real;
			return Type.Real;
		}
	}
	
	/**
	 * Ծրագրի տեքստի տողի համարը։
	 */
	public int line = 0;
}

