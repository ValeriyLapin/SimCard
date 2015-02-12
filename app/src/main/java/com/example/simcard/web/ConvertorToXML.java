package com.example.simcard.web;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.Log;

import com.example.simcard.db.Card;
import com.example.simcard.db.SQLiteAdapter;
import com.example.simcard.db.SaleOrder;

public class ConvertorToXML {
	private ConvertorToXML() {

	}

	public static String getXml(List<SaleOrder> list) {
		if (list == null || list.size() == 0)
			return null;

		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();

			Element rootElement = doc.createElement("SaleOrderCollection");
			doc.appendChild(rootElement);

			Element saleOrdersElement = doc.createElement("SaleOrders");
			rootElement.appendChild(saleOrdersElement);

			for (SaleOrder order : list) {
				addOneOrder(doc, saleOrdersElement, order);
			}

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			xmlString = output.toString();
			// xmlResult.setText();

		} catch (ParserConfigurationException e) {
			Log.e("getXml",
					"ParserConfigurationException: \n"
							+ e.getLocalizedMessage());
		} catch (TransformerConfigurationException e) {
			Log.e("getXml",
					"TransformerConfigurationException: \n"
							+ e.getLocalizedMessage());
		} catch (TransformerException e) {
			Log.e("getXml",
					"TransformerException: \n" + e.getLocalizedMessage());
		}
		
		return xmlString;
	}

	private static void addOneOrder(Document doc, Element saleOrdersElement,
			SaleOrder order) {
		Element saleOrder = doc.createElement("SaleOrder");
		saleOrdersElement.appendChild(saleOrder);

		Element dt = doc.createElement("dt");
		saleOrder.appendChild(dt);
		dt.appendChild(doc.createTextNode(order.getDtString()));

		Element agentId = doc.createElement("AgentId");
		saleOrder.appendChild(agentId);
		agentId.appendChild(doc.createTextNode(String.valueOf(order
				.getAgentId())));

		Element buyerId = doc.createElement("BuyerId");
		saleOrder.appendChild(buyerId);
		buyerId.appendChild(doc.createTextNode(String.valueOf(order
				.getBuyerId())));

		Element locationId = doc.createElement("LocationId");
		saleOrder.appendChild(locationId);
		locationId.appendChild(doc.createTextNode(String.valueOf(order
				.getLocationId())));

		if (!order.Description.isEmpty()) {
			Element description = doc.createElement("Description");
			saleOrder.appendChild(description);
			description.appendChild(doc.createTextNode(order.Description));
		}

		Element orderType = doc.createElement("OrderType");
		saleOrder.appendChild(orderType);
		orderType.appendChild(doc.createTextNode(String
				.valueOf(order.OrderType)));

		Element orderStatus = doc.createElement("OrderStatus");
		saleOrder.appendChild(orderStatus);
		orderStatus.appendChild(doc.createTextNode(String
				.valueOf(order.OrderStatus)));

		Element uId = doc.createElement("UId");
		saleOrder.appendChild(uId);
		uId.appendChild(doc.createTextNode(order.getUId()));

		// Element id = doc.createElement("Id");
		// saleOrder.appendChild(id);
		// id.appendChild(doc.createTextNode(""));

		Element cards = doc.createElement("Cards");
		saleOrder.appendChild(cards);

		if (order.listOfCards == null) {
			order.listOfCards = SQLiteAdapter.getListOfCards(order);
		}

		for (Card one_card : order.listOfCards) {
			final Element card = doc.createElement("Card");
			cards.appendChild(card);

			final Element sn = doc.createElement("Sn");
			card.appendChild(sn);
			sn.appendChild(doc.createTextNode(one_card.Sn));

			// if ( OrderType==2) is scratch card order
			if (one_card.getNominalId() != -1) {
				Element nominalId = doc.createElement("NominalId");
				card.appendChild(nominalId);

				nominalId.appendChild(doc.createTextNode(String
						.valueOf(one_card.getNominalId())));
			}
		}
	}
}

// <?xml version="1.0" encoding="UTF-8" ?>
// <SaleOrderCollection>
// <SaleOrders>
// <SaleOrder>
// <dt>29/09/2014</dt>
// <AgentId>6047</AgentId>
// <BuyerId>37539</BuyerId>
// <LocationId>32732</LocationId>
// <Description></Description>
// <OrderType>2</OrderType>
// <OrderStatus>1</OrderStatus>
// <UId>fbf17b64-6d29-4397-8299-5fd966fffac9</UId>
// <Id></Id>
// <Cards>
// <Card>
// <Sn>1</Sn>
// <NominalId>156</NominalId>
// </Card>
// <Card>
// <Sn>2</Sn>
// <NominalId>156</NominalId>
// </Card>
// </Cards>
// </SaleOrder>
// </SaleOrders>
// </SaleOrderCollection>
