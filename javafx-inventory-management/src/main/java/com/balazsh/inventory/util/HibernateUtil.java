package com.balazsh.inventory.util;

import com.balazsh.inventory.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jboss.logging.Logger;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry registry;
    private static final Logger logger = Logger.getLogger(HibernateUtil.class);

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                registry = new StandardServiceRegistryBuilder().loadProperties("hibernate.properties").build();

                MetadataSources metadataSources = new MetadataSources(registry);

                metadataSources.addAnnotatedClass(Image.class);
                metadataSources.addAnnotatedClass(User.class);
                metadataSources.addAnnotatedClass(Product.class);
                metadataSources.addAnnotatedClass(ProductImage.class);
                metadataSources.addAnnotatedClass(Listing.class);
                metadataSources.addAnnotatedClass(ProductCategory.class);
                metadataSources.addAnnotatedClass(Role.class);
                metadataSources.addAnnotatedClass(RolePermission.class);
                metadataSources.addAnnotatedClass(Stock.class);
                metadataSources.addAnnotatedClass(UserRole.class);
                metadataSources.addAnnotatedClass(Resource.class);
                metadataSources.addAnnotatedClass(Operation.class);
                metadataSources.addAnnotatedClass(Permission.class);
                metadataSources.addAnnotatedClass(PermissionId.class);
                metadataSources.addAnnotatedClass(ProductImageId.class);
                metadataSources.addAnnotatedClass(Order.class);
                metadataSources.addAnnotatedClass(Transaction.class);

                Metadata metadata = metadataSources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
                logger.info("Hibernate SessionFactory created");

            }catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                    logger.info("Hibernate SessionFactory destroyed");
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown(){
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
            logger.info("Hibernate SessionFactory destroyed");
        }
    }
}
