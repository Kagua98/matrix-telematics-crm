package crm.matrixtelematic.com.views.clients;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import crm.matrixtelematic.com.data.entity.Clients;
import crm.matrixtelematic.com.data.service.ClientsService;
import crm.matrixtelematic.com.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Clients")
@Route(value = "clients/:clientsID?/:action?(edit)", layout = MainLayout.class)
public class ClientsView extends Div implements BeforeEnterObserver {

    private final String CLIENTS_ID = "clientsID";
    private final String CLIENTS_EDIT_ROUTE_TEMPLATE = "clients/%s/edit";

    private Grid<Clients> grid = new Grid<>(Clients.class, false);

    private TextField clientName;
    private TextField kraPin;
    private TextField contact;
    private TextField address;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Clients> binder;

    private Clients clients;

    private final ClientsService clientsService;

    @Autowired
    public ClientsView(ClientsService clientsService) {
        this.clientsService = clientsService;
        addClassNames("clients-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("clientName").setAutoWidth(true);
        grid.addColumn("kraPin").setAutoWidth(true);
        grid.addColumn("contact").setAutoWidth(true);
        grid.addColumn("address").setAutoWidth(true);
        grid.setItems(query -> clientsService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CLIENTS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ClientsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Clients.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.clients == null) {
                    this.clients = new Clients();
                }
                binder.writeBean(this.clients);
                clientsService.update(this.clients);
                clearForm();
                refreshGrid();
                Notification.show("Clients details stored.");
                UI.getCurrent().navigate(ClientsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the clients details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> clientsId = event.getRouteParameters().get(CLIENTS_ID).map(UUID::fromString);
        if (clientsId.isPresent()) {
            Optional<Clients> clientsFromBackend = clientsService.get(clientsId.get());
            if (clientsFromBackend.isPresent()) {
                populateForm(clientsFromBackend.get());
            } else {
                Notification.show(String.format("The requested clients was not found, ID = %s", clientsId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ClientsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        clientName = new TextField("Client Name");
        kraPin = new TextField("Kra Pin");
        contact = new TextField("Contact");
        address = new TextField("Address");
        Component[] fields = new Component[]{clientName, kraPin, contact, address};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Clients value) {
        this.clients = value;
        binder.readBean(this.clients);

    }
}
