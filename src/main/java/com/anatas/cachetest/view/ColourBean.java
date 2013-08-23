package com.anatas.cachetest.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anatas.cachetest.model.Colour;
import com.anatas.cachetest.spring.CacheProvider;

/**
 * Backing bean for Colour entities.
 * <p>
 * This class provides CRUD functionality for all Colour entities. It focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for state management,
 * <tt>PersistenceContext</tt> for persistence, <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ColourBean implements Serializable {
    private static final Logger logger           = LoggerFactory.getLogger(ColourBean.class);
    private static final long   serialVersionUID = 1L;
    @Inject
    private CacheProvider       cacheProvider;

    /*
     * Support creating and retrieving Colour entities
     */

    public ColourBean() {
    }

    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Colour colour;

    public Colour getColour() {
        return this.colour;
    }

    private long time;

    public long getTime() {
        return time;
    }

    @Inject
    private Conversation  conversation;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String create() {

        this.conversation.begin();
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
        }

        if (this.id == null) {
            this.colour = this.example;
        } else {
            this.colour = findById(getId());
        }
    }

    public Colour findById(Long id) {
        long before = System.currentTimeMillis();
        try {
            Element elem = cacheProvider.getColour(id);
            if (elem == null) {
                Colour colour = this.entityManager.find(Colour.class, id);
                if (colour == null) {
                    return null;
                }
                cacheProvider.putColour(elem = new Element(id, colour));
            }

            return (Colour) elem.getObjectValue();
        } finally {
            long after = System.currentTimeMillis();
            time = after - before;
        }
    }

    /*
     * Support updating and deleting Colour entities
     */

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.colour);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.colour);
                return "view?faces-redirect=true&id=" + this.colour.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Colour deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    /*
     * Support searching Colour entities with pagination
     */

    private int          page;
    private long         count;
    private List<Colour> pageItems;

    private Colour       example = new Colour();

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public Colour getExample() {
        return this.example;
    }

    public void setExample(Colour example) {
        this.example = example;
    }

    public void search() {
        this.page = 0;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Colour> root = countCriteria.from(Colour.class);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Colour> criteria = builder.createQuery(Colour.class);
        root = criteria.from(Colour.class);
        TypedQuery<Colour> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Colour> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String name = this.example.getName();
        if (name != null && !"".equals(name)) {
            predicatesList.add(builder.like(root.<String> get("name"), '%' + name + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<Colour> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    /*
     * Support listing and POSTing back Colour entities (e.g. from inside an HtmlSelectOneMenu)
     */

    public List<Colour> getAll() {
        System.out.println(String.format("cacheProvider %s", cacheProvider));

        CriteriaQuery<Colour> criteria = this.entityManager.getCriteriaBuilder().createQuery(Colour.class);
        return this.entityManager.createQuery(criteria.select(criteria.from(Colour.class))).getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

        final ColourBean ejbProxy = this.sessionContext.getBusinessObject(ColourBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((Colour) value).getId());
            }
        };
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */

    private Colour add = new Colour();

    public Colour getAdd() {
        return this.add;
    }

    public Colour getAdded() {
        Colour added = this.add;
        this.add = new Colour();
        return added;
    }
}