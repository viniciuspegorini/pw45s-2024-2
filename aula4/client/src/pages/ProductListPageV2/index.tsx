import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import ProductService from "../../services/ProductService";
import {
  Table,
  Thead,
  Tbody,
  Tfoot,
  Tr,
  Th,
  Td,
  TableCaption,
  TableContainer,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  IconButton,
} from "@chakra-ui/react";
import {
  BsThreeDotsVertical,
  BsPencilSquare,
  BsTrash,
  BsPlusCircle,
} from "react-icons/bs";
import { Product } from "../../commons/types";

export function ProductListPageV2() {
  const [data, setData] = useState([]);
  const [apiError, setApiError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = () => {
    ProductService.findAll()
      .then((response) => {
        setData(response.data);
        setApiError("");
      })
      .catch((error) => {
        setApiError("Falha ao carregar a lista de produtos.");
      });
  };

  const onEdit = (url: string) => {
    navigate(url);
  };

  const onRemove = (id: number) => {
    ProductService.remove(id)
      .then((response) => {
        loadData();
        setApiError("");
      })
      .catch((error) => {
        setApiError("Falha ao remover o produto.");
      });
  };

  return (
    <div className="container">
      <h1 className="fs-2 mb-4 text-center">Lista de Produto V2</h1>
      <div className="text-center">
        <Link
          className="btn btn-success btn-icon mb-2"
          to="/product-v2/new"
          title="Novo Produto"
        >
          <BsPlusCircle /> <span style={{ marginLeft: 10 }}>Novo Produto</span>
        </Link>
      </div>
      <TableContainer>
        <Table>
          <TableCaption>Lista de Produtos</TableCaption>
          <Thead>
            <Tr>
              <Th>#</Th>
              <Th>Nome</Th>
              <Th isNumeric>Preço</Th>
              <Th>Categoria</Th>
              <Th>Ações</Th>
            </Tr>
          </Thead>
          <Tbody>
            {data.map((product: Product) => (
              <Tr
                key={product.id}
                _hover={{ cursor: "pointer", background: "#eee" }}
              >
                <Td>{product.id}</Td>
                <Td>{product.name}</Td>
                <Td isNumeric>{product.price}</Td>
                <Td>{product.category?.name}</Td>
                <Td>
                  <Menu>
                    <MenuButton
                      as={IconButton}
                      aria-label="Actions"
                      icon={<BsThreeDotsVertical size={20} />}
                      variant="ghost"
                    />
                    <MenuList>
                      <MenuItem
                        icon={<BsPencilSquare />}
                        onClick={() => onEdit(`/product-v2/${product.id}`)}
                      >
                        Editar
                      </MenuItem>
                      <MenuItem
                        icon={<BsTrash />}
                        onClick={() => onRemove(product.id!)}
                      >
                        Remover
                      </MenuItem>
                    </MenuList>
                  </Menu>
                </Td>
              </Tr>
            ))}
          </Tbody>
          <Tfoot>
            <Tr>
              <Th>#</Th>
              <Th>Nome</Th>
              <Th isNumeric>Preço</Th>
              <Th>Categoria</Th>
              <Th>Ações</Th>
            </Tr>
          </Tfoot>
        </Table>
      </TableContainer>
      {apiError && <div className="alert alert-danger">{apiError}</div>}
    </div>
  );
}
