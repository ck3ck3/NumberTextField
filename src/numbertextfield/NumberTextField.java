/*******************************************************************************
 *	NumberTextField
 *  Copyright (C) 2017  ck3ck3
 *  https://github.com/ck3ck3/NumberTextField
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package numbertextfield;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField
{
	@FXML
	protected TextField numtxtField;
	
	protected Integer minValue = null;
	protected Integer maxValue = null;
	protected boolean allowEmpty = false;
	protected List<String> allowedWords;
	protected ChangeListener<Boolean> focusedPropertyListenr;
	protected SimpleBooleanProperty validProperty = new SimpleBooleanProperty(true);
	protected String backgroundColorForValidText;
	protected String backgroundColorForInvalidText;
	protected String textColorForValidText;
	protected String textColorForInvalidText;
	protected boolean colorsAreSet = false; 

	
	public NumberTextField()
	{
		this(null, null, null);
	}

	public NumberTextField(String text)
	{
		this(text, null, null);
	}

	public NumberTextField(String text, Integer minValue)
	{
		this(text, minValue, null);
	}
	
	public NumberTextField(Integer minValue)
	{
		this("", minValue, null);
	}
	
	public NumberTextField(Integer minValue, Integer maxValue)
	{
		this("", minValue, maxValue);
	}

	public NumberTextField(String text, Integer minValue, Integer maxValue)
	{
		super(text);
		
		numtxtField = this;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/NumberTextField.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try 
        {
            fxmlLoader.load();
        } 
        catch (IOException exception) 
        {
            throw new RuntimeException(exception);
        }
		
		
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);

		focusedPropertyListenr = generateFocusedPropertyListenr();
		this.focusedProperty().addListener(focusedPropertyListenr);
		
		this.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isValid = validate(newValue);
			validProperty.set(isValid);
			
			if (colorsAreSet)
			{
				String style = "-fx-text-fill: " + (isValid ? textColorForValidText : textColorForInvalidText) + ";";
				style += "\n-fx-control-inner-background: " + (isValid ? backgroundColorForValidText : backgroundColorForInvalidText) + ";";
				this.setStyle(style);
			}
		});
	}
	
	public void removeFocusValidator()
	{
		this.focusedProperty().removeListener(focusedPropertyListenr);
	}
	
	public void setFocusValidator()
	{
		this.focusedProperty().addListener(focusedPropertyListenr);
	}
	
	protected ChangeListener<Boolean> generateFocusedPropertyListenr()
	{
		return new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (!newPropertyValue) //focus out
				{
					if (!validate(getText()))
					{
						String validRange = "";

						if (minValue != null && maxValue != null)
							validRange = "between " + minValue + " and " + maxValue;
						else
							if (minValue != null)
								validRange = ">= " + minValue;
							else
								if (maxValue != null)
									validRange = "<= " + maxValue;

						String msg = (getText() == null ? "Please enter a number " + validRange : "Input \"" + getText() + "\" is not a number " + validRange);
						if (allowedWords != null)
							msg += ", or one of the allowed values " + getStringOfAllowedWords();
						
						new Alert(AlertType.ERROR, msg).showAndWait();
						requestFocus();
					}
				}
			}

			private String getStringOfAllowedWords()
			{
				StringBuilder builder = new StringBuilder("(");
				
				for (String word : allowedWords)
					builder.append("\"" + word + "\", ");
				
				builder.delete(builder.lastIndexOf(", "), builder.length());
				builder.append("}");
				
				return builder.toString();
			}
		};
	}

	protected boolean validate(String text)
	{
		if (text == null || text.isEmpty())
			return allowEmpty;

		if (allowedWords != null && allowedWords.contains(text))
			return true;
		
		int value;
		try
		{
			value = Integer.valueOf(text);
		}
		catch (NumberFormatException nfe) //not a number
		{
			return false;
		}

		if (getMinValue() != null && value < getMinValue())
			return false;

		if (getMaxValue() != null && value > getMaxValue())
			return false;

		return true;
	}
	
	public boolean isValidText()
	{
		return validate(getText());
	}
	
	public void setAllowedWords(List<String> words)
	{
		allowedWords = new ArrayList<>(words);
	}
	
	public List<String> getAllowedWords()
	{
		return allowedWords;
	}

	public Integer getValue()
	{
		String text = getText();
		
		return (text == null || text.isEmpty() ? null : Integer.valueOf(text));
	}

	public Integer getMinValue()
	{
		return minValue;
	}

	public void setMinValue(Integer minValue)
	{
		this.minValue = minValue;
	}

	public Integer getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(Integer maxValue)
	{
		this.maxValue = maxValue;
	}

	public boolean isAllowEmpty()
	{
		return allowEmpty;
	}

	public void setAllowEmpty(boolean allowEmpty)
	{
		this.allowEmpty = allowEmpty;
	}

	public SimpleBooleanProperty getValidProperty()
	{
		return validProperty;
	}

	public String getTextColorForValidText()
	{
		return textColorForValidText;
	}
	
	public String getTextColorForInvalidText()
	{
		return textColorForInvalidText;
	}
	
	public String getBackgroundColorForValidText()
	{
		return backgroundColorForValidText;
	}
	
	public String getBackgroundColorForInvalidText()
	{
		return backgroundColorForInvalidText;
	}	

	public void setColorForText(String textColorForValidText, String backgroundColorForValidText, String textColorForInvalidText, String backgroundColorForInvalidText)
	{
		colorsAreSet = true;
		this.textColorForValidText = textColorForValidText;
		this.textColorForInvalidText = textColorForInvalidText;
		this.backgroundColorForValidText = backgroundColorForValidText;
		this.backgroundColorForInvalidText = backgroundColorForInvalidText;
	}
}
