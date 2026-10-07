create schema evoting;
use evoting;
create table voting_user(
	user_id int auto_increment primary key,
    username varchar(100) unique not null,
    password_hash text not null,
    user_role enum('ORGANIZATOR', 'GLASAC') not null,
    organization_name varchar(100),
    id_number varchar(50),
    first_name varchar(50),
    last_name varchar(50),
    certificate_serial_number varchar(100) unique not null,
    failed_attempts int default 0,
    is_revoked tinyint(1) default 0,
    created_at timestamp default current_timestamp
);

create table voting(
	voting_id int auto_increment primary key,
    organizer_id int not null,
    title varchar(200) not null,
    voting_description text,
    start_time datetime not null,
    end_time datetime not null,
    options_json JSON not null,
    constraint fk_organizer_voting
    foreign key (organizer_id) references voting_user(user_id)
);

create table vote_metadata(
	metadata_id int auto_increment primary key,
    voting_id int not null,
    voter_id int not null,
    voting_timestamp datetime not null,
    hmac_signature varchar(200) not null,
    constraint fk_voting_metadeta
    foreign key (voting_id) references voting(voting_id),
    constraint fk_voter_metadata
    foreign key (voter_id) references voting_user(user_id)
);

create table encrypted_votes(
	vote_id int auto_increment primary key,
    voting_id int not null,
    encrypted_vote longtext not null,
    encrypted_sym_key longtext not null,
    voter_signature longtext not null,
    vote_receipt_hash varchar(100) not null,
    constraint fk_voting_votesvoting_user
    foreign key (voting_id) references voting(voting_id)
);

drop TABLE vote_metadata, encrypted_votes, voting, voting_user;

select * from voting ;
