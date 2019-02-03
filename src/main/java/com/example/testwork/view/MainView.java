package com.example.testwork.view;

import com.example.testwork.captcha.Captcha;
import com.example.testwork.captcha.CapthaImpl;
import com.example.testwork.entity.GuestBook;
import com.example.testwork.repository.GuestBookRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.imageio.ImageIO;
import javax.swing.text.html.parser.ContentModel;

import static com.vaadin.flow.component.Tag.P;


@Route
public class MainView extends VerticalLayout {
    private static final int MAX_ITEM_ON_PAGE = 25;
    private String orderBy = "";
    private Page<GuestBook> guestBooks;
    private Grid<GuestBook> grid = new Grid<>();
    private GuestBookRepository guestBookRepository;

    @Autowired
    public MainView(GuestBookRepository guestBookRepository) {
        this.guestBookRepository = guestBookRepository;
        add(new Button("Send message.", i -> openDialogToSendMessage()));

        addNavigateBar(this);
        addMessage(this);
        setHeight("100vh");
    }

    private void addMessage(MainView mainView){
        grid.setWidth("100%");
        grid.addColumn(new ComponentRenderer<>(guestBook -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setWidth("100%");
            layout.add(new Label("Name: " + guestBook.getUserName()));
            layout.add(new Label("E-Mail: " + guestBook.geteMail()));
            return layout;
        })).setHeader("Info").setWidth("20%").setFlexGrow(0).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(guestBook -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new Label("Date " + guestBook.getDate()));
            Div div = new Div();
            div.setText("Message: " + guestBook.getText());
            div.getStyle().set("white-space", "normal");
            layout.add(div);
            return layout;
        })).setHeader("Message").setWidth("80%");
        updateList(0);
        mainView.add(grid);
    }

    private void addNavigateBar(MainView mainView){
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button leftButton = new Button("Left", new Icon(VaadinIcon.ARROW_LEFT), event -> updateList(-1));
        Button rightButton = new Button("Right", new Icon(VaadinIcon.ARROW_RIGHT), event -> updateList(1));
        rightButton.setIconAfterText(true);
        //ComboBox
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Without order", "Order by name", "Order by date");
        comboBox.addValueChangeListener(event -> {
            if (event.getValue().equals("Without order")){
                setOrderBy("");
            }else if (event.getValue().equals("Order by name")){
                setOrderBy("name");
            }else if (event.getValue().equals("Order by date")) setOrderBy("date");
            updateList(0);
        });
        //end ComboBox
        horizontalLayout.add(leftButton, rightButton, comboBox);
        mainView.add(horizontalLayout);
    }

    private void updateList(int orientationUpdate){
        switch (orientationUpdate){
            case 0: guestBooks = getRepository(PageRequest.of(0, MAX_ITEM_ON_PAGE));
                    break;
            case 1: if (guestBooks.isLast())break;
                    guestBooks = getRepository(guestBooks.nextPageable());
                    break;
            case -1:if (guestBooks.isFirst())break;
                    guestBooks = getRepository(guestBooks.previousPageable());
                    break;
        }
        grid.setItems(guestBooks.getContent());
    }

    private void setOrderBy(String orderBy){
        this.orderBy = orderBy;
    }

    private Page getRepository(Pageable pageable){
        if (orderBy.equals("name")){
            return guestBookRepository.findAllByOrderByUserNameAsc(pageable);
        }else if (orderBy.equals("date")){
            return guestBookRepository.findAllByOrderByDateAsc(pageable);
        }else return guestBookRepository.findAll(pageable);
    }

    private void openDialogToSendMessage(){
        Binder<GuestBook> binder = new Binder<>(GuestBook.class);
        Captcha captcha = new CapthaImpl();
        String IP = UI.getCurrent().getSession().getBrowser().getAddress();
        String browser = UI.getCurrent().getSession().getBrowser().getBrowserApplication();
        //Create the dialog window
        Dialog dialog = new Dialog();
            dialog.setCloseOnOutsideClick(false);
        VerticalLayout layoutDialog = new VerticalLayout();
        TextField nameFromDialog = new TextField("Your name:");
        binder.forField(nameFromDialog).withValidator(str -> str.trim().length() >= 3, "Please enter the name.").bind(GuestBook::getUserName, GuestBook::setUserName);
        TextField eMailFromDialog = new TextField("Your e-mail:");
        binder.forField(eMailFromDialog).withValidator(new EmailValidator("This doesn't look like a valid email address.")).bind(GuestBook::geteMail, GuestBook::seteMail);
        TextArea textArea = new TextArea("Your message:");
            textArea.setWidth("50vh");
            textArea.setHeight("20vh");
            textArea.setRequired(true);
        Image image = captcha.getCaptchaImg();
        TextField captchaAnswerFromDialog = new TextField("Enter captcha string:");
        captchaAnswerFromDialog.setErrorMessage("The characters you entered do not match!");
        captchaAnswerFromDialog.setRequired(true);
        binder.bindInstanceFields(this);
        layoutDialog.add(nameFromDialog, eMailFromDialog, image, captchaAnswerFromDialog, textArea);
        //Button Send
        NativeButton confirmButton = new NativeButton("Send", event -> {
            if (captcha.checkUserAnswer(captchaAnswerFromDialog.getValue())){
            guestBookRepository.save(new GuestBook(nameFromDialog.getValue(),
                    eMailFromDialog.getValue(),
                    textArea.getValue(),
                    IP,
                    browser));
            updateList(0);
            dialog.close(); }else captchaAnswerFromDialog.getErrorMessage();
        });
        NativeButton cancelButton = new NativeButton("Cancel", event -> {
            Notification.show("Cancel...");
            dialog.close();
        });
        dialog.add(layoutDialog, cancelButton,confirmButton);
        dialog.open();
    }
}
